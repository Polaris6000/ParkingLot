package com.example.parkinglot.service;

import com.example.parkinglot.dao.FeePolicyDAO;
import com.example.parkinglot.dao.MonthlyParkingDAO;
import com.example.parkinglot.dao.PayLogsDAO;
import com.example.parkinglot.dto.FeePolicyDTO;
import com.example.parkinglot.dto.MonthlyParkingDTO;
import com.example.parkinglot.vo.PayLogsVO;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
// 월정액 회원 관리 서비스
// 싱글톤 패턴 적용
public enum MonthlyParkingService {
    INSTANCE;

    private final MonthlyParkingDAO monthlyParkingDAO;
    private final PayLogsDAO payLogsDAO;
    private final FeePolicyDAO feePolicyDAO;

    // 허용된 정렬 컬럼 whitelist (SQL Injection 방지)
    // Controller에서 넘어온 값이 이 목록에 없으면 기본값(begin_date)으로 대체
    private static final Set<String> ALLOWED_SORT_COLUMNS = new HashSet<>(
            Arrays.asList("id", "plate_number", "name", "phone_number", "begin_date", "expiry_date")
    );
    public static final int DEFAULT_PAGE_SIZE = 10;

    MonthlyParkingService() {
        this.monthlyParkingDAO = new MonthlyParkingDAO();
        this.payLogsDAO = new PayLogsDAO();
        this.feePolicyDAO = new FeePolicyDAO();
    }

    // ──────────────────────────────────────────────
    // 핵심 비즈니스 로직
    // ──────────────────────────────────────────────

    // 월정액 등록
    // months 개월 수만큼 행을 분리해서 INSERT
    // 등록 성공 시 pay_logs에도 월별 1건씩 기록
    public boolean register(MonthlyParkingDTO dto, int months) throws SQLException {
        log.info("월정액 등록 요청: {}, {}개월", dto.getPlateNumber(), months);

        // 1. 등록하려는 전체 기간의 마지막 날 계산
        LocalDate totalEnd = calcExpiryDate(dto.getBeginDate(), months);

        // 2. 날짜 겹침 중복 검사
        if (monthlyParkingDAO.countOverlap(dto.getPlateNumber(), dto.getBeginDate(), totalEnd) > 0) {
            log.info("날짜 겹침 중복: {}", dto.getPlateNumber());
            return false;
        }

        // 3. 개월 수만큼 행 분리
        List<MonthlyParkingDTO> rows = splitByMonths(dto, months);

        // 4. 요금 정책에서 월주차 비용 조회
        FeePolicyDTO policy = feePolicyDAO.selectCurrentPolicy();

        // 5. 분리된 행 각각 INSERT + pay_logs 기록
        for (MonthlyParkingDTO row : rows) {
            monthlyParkingDAO.insert(row);
            log.info("월정액 행 등록: {} {} ~ {}", row.getPlateNumber(), row.getBeginDate(), row.getExpiryDate());

            payLogsDAO.insert(PayLogsVO.builder()
                    .payTime(LocalDateTime.now())
                    .kindOfDiscount("monthly")
                    .payLog(policy.getMonthlyPay())
                    .build());
        }

        return true;
    }

    // 차량번호로 다음 시작일 반환 (연장 시 사용)
    // 등록 이력 없으면 오늘 날짜 반환 (신규)
    public LocalDate getNextBeginDate(String plateNumber) {
        MonthlyParkingDTO latest = monthlyParkingDAO.selectLatestByPlate(plateNumber);
        if (latest == null) {
            log.info("신규 회원: {}", plateNumber);
            return LocalDate.now();
        }
        log.info("연장 회원: {} 기존 만료일 {}", plateNumber, latest.getExpiryDate());
        return latest.getExpiryDate().plusDays(1);
    }

    // 차량번호로 최근 회원 정보 조회 (연장 폼 자동 입력용)
    public MonthlyParkingDTO getLatestByPlate(String plateNumber) {
        return monthlyParkingDAO.selectLatestByPlate(plateNumber);
    }

    // 차량번호로 전체 이력 조회
    public List<MonthlyParkingDTO> getHistoryByPlate(String plateNumber) {
        return monthlyParkingDAO.selectAllByPlate(plateNumber);
    }

    // ──────────────────────────────────────────────
    // CRUD
    // ──────────────────────────────────────────────

    // id로 회원 조회
    public MonthlyParkingDTO getById(int id) {
        return monthlyParkingDAO.selectById(id);
    }

    // 페이징 + 정렬 목록 조회
    public List<MonthlyParkingDTO> getPagedList(int page, int pageSize,
            String sortColumn, String sortOrder) throws SQLException {
        int offset = (page - 1) * pageSize;
        return monthlyParkingDAO.selectWithPaging(offset, pageSize,
                sanitizeSortColumn(sortColumn), sanitizeSortOrder(sortOrder));
    }

    // 검색 + 정렬 + 페이징 목록 조회
    // keyword가 null 또는 빈 문자열이면 전체 조회와 동일하게 동작
    public List<MonthlyParkingDTO> getPagedListBySearch(int page, int pageSize,
            String keyword, String sortColumn, String sortOrder) throws SQLException {
        int offset = (page - 1) * pageSize;
        return monthlyParkingDAO.selectWithPagingAndSearch(offset, pageSize, keyword,
                sanitizeSortColumn(sortColumn), sanitizeSortOrder(sortOrder));
    }

    // 전체 페이지 수 계산
    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) monthlyParkingDAO.getTotalCount() / pageSize);
    }

    // 검색 조건 포함 전체 페이지 수 계산
    public int getTotalPagesBySearch(String keyword, int pageSize) {
        return (int) Math.ceil((double) monthlyParkingDAO.getTotalCountBySearch(keyword) / pageSize);
    }

    // 전체 회원 수
    public int getTotalCount() {
        return monthlyParkingDAO.getTotalCount();
    }

    // 검색 조건 포함 전체 회원 수
    public int getTotalCountBySearch(String keyword) {
        return monthlyParkingDAO.getTotalCountBySearch(keyword);
    }

    // 회원 정보 수정
    public boolean update(MonthlyParkingDTO dto) {
        log.info("회원 수정 요청: {}", dto);
        return monthlyParkingDAO.update(dto);
    }

    // 회원 삭제
    public void delete(int id) {
        log.info("회원 삭제 요청 (id={})", id);
        monthlyParkingDAO.delete(id);
    }

    // 월정액 유효 회원 여부 확인
    public boolean isValidMember(String plateNumber) {
        return monthlyParkingDAO.isValidMember(plateNumber);
    }

    // ──────────────────────────────────────────────
    // private 헬퍼
    // ──────────────────────────────────────────────

    // 시작일부터 months개월치 행 분리
    // ex) beginDate=3/5, months=3 → [3/5~4/4, 4/5~5/4, 5/5~6/4]
    private List<MonthlyParkingDTO> splitByMonths(MonthlyParkingDTO base, int months) {
        List<MonthlyParkingDTO> list = new ArrayList<>();
        LocalDate begin = base.getBeginDate();
        for (int i = 0; i < months; i++) {
            LocalDate expiry = calcExpiryDate(begin, 1);
            list.add(MonthlyParkingDTO.builder()
                    .plateNumber(base.getPlateNumber())
                    .name(base.getName())
                    .phoneNumber(base.getPhoneNumber())
                    .beginDate(begin)
                    .expiryDate(expiry)
                    .build());
            begin = expiry.plusDays(1); // 다음 달 시작일
        }
        return list;
    }

    // 만료일 계산: 익월 같은 날 -1일
    // ex) 3/5 → 4/4 / 1/31 → 2/27(윤년 자동 처리)
    private LocalDate calcExpiryDate(LocalDate begin, int months) {
        return begin.plusMonths(months).minusDays(1);
    }

    // 정렬 컬럼 whitelist 검증: 허용 목록에 없으면 기본값 반환
    private String sanitizeSortColumn(String sortColumn) {
        if (sortColumn == null || !ALLOWED_SORT_COLUMNS.contains(sortColumn)) return "begin_date";
        return sortColumn;
    }

    // 정렬 방향 검증: ASC/DESC 외의 값은 ASC로 대체
    private String sanitizeSortOrder(String sortOrder) {
        if ("DESC".equalsIgnoreCase(sortOrder)) return "DESC";
        return "ASC";
    }
}