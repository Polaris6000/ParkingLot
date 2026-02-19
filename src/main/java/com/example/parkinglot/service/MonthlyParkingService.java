package com.example.parkinglot.service;

import com.example.parkinglot.dao.MonthlyParkingDAO;
import com.example.parkinglot.dto.MonthlyParkingDTO;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.util.List;

@Log4j2

//월정액 회원 관리 서비스
// - 회원 CRUD 비즈니스 로직 처리
// 싱글톤 패턴 적용

public enum MonthlyParkingService {
    INSTANCE;

    private final MonthlyParkingDAO monthlyParkingDAO;

    // 허용된 정렬 컬럼 whitelist (SQL Injection 방지)
    // JSP/Controller 에서 넘어온 값이 이 목록에 없으면 기본값(begin_date)으로 대체
    private static final java.util.Set<String> ALLOWED_SORT_COLUMNS = new java.util.HashSet<>(
            java.util.Arrays.asList("id", "plate_number", "name", "phone_number", "begin_date", "expiry_date")
    );
    public static final int DEFAULT_PAGE_SIZE = 10; // 기본 페이지당 표시 건수


    MonthlyParkingService() {
        this.monthlyParkingDAO = new MonthlyParkingDAO();
    }

    // 정렬 컬럼 whitelist 검증: 허용 목록에 없으면 기본값 반환
    private String sanitizeSortColumn(String sortColumn) {
        if (sortColumn == null || !ALLOWED_SORT_COLUMNS.contains(sortColumn)) {
            return "begin_date"; // 기본 정렬: 시작일 오름차순
        }
        return sortColumn;
    }

    // 정렬 방향 검증: ASC/DESC 외의 값은 ASC 로 대체
    private String sanitizeSortOrder(String sortOrder) {
        if ("DESC".equalsIgnoreCase(sortOrder)) return "DESC";
        return "ASC";
    }

    //회원 등록(차량 번호 중복 검사 포함)
    //동일 차량 번호가 이미 존재하면 등록 거부
    public boolean register(MonthlyParkingDTO monthlyParkingDTO) throws SQLException {
        log.info("회원 등록 요청 : {}", monthlyParkingDTO);

        if (monthlyParkingDAO.selectByPlateNumber(monthlyParkingDTO.getPlateNumber()) != null) {
            log.info("차량 중복 번호 : {}", monthlyParkingDTO.getPlateNumber());
            return false;
        }
        monthlyParkingDAO.insert(monthlyParkingDTO);
        log.info("회원 등록 완료 : {}", monthlyParkingDTO.getPlateNumber());
        return true;
    }

    // 페이징 + 정렬 처리된 회원 목록 조회
    public List<MonthlyParkingDTO> getPagedList(int page, int pageSize, String sortColumn, String sortOrder) throws SQLException {
        int offset = (page - 1) * pageSize; //LIMIT 시작 위치 계산
        return monthlyParkingDAO.selectWithPaging(offset, pageSize,
                sanitizeSortColumn(sortColumn), sanitizeSortOrder(sortOrder));
    }

    //전체 회원 목록 조회 (페이징 x)
    public List<MonthlyParkingDTO> getAllList() throws SQLException {
        return monthlyParkingDAO.selectAll();
    }

    // 전체 페이지 수 계산 (pageSize 동적 처리)
    public int getTotalPages(int pageSize) throws SQLException {
        int totalCount = monthlyParkingDAO.getTotalCount();
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    //전체 회원 수
    public int getTotalCount() throws SQLException {
        return monthlyParkingDAO.getTotalCount();
    }

    //Id로 회원 조회
    public MonthlyParkingDTO getById(int id) throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingDAO.selectById(id);
        log.info("회원 조회 (id={}) : {}", id, monthlyParkingDTO);
        return monthlyParkingDTO;
    }

    //차량 번호로 회원 조회
    public MonthlyParkingDTO getByPlateNumber(String plateNumber) throws SQLException {
        return monthlyParkingDAO.selectByPlateNumber(plateNumber);
    }

    //회원 정보 수정
    public boolean update(MonthlyParkingDTO monthlyParkingDTO) throws SQLException {
        log.info("회원 수정 요청 : {}", monthlyParkingDTO);
        return monthlyParkingDAO.update(monthlyParkingDTO);
    }

    //회원 삭제
    public void delete(int id) throws SQLException {
        log.info("회원 삭제 요청 (id={}) : ", id);
        monthlyParkingDAO.delete(id);
    }

    //월정액 유효 회원 여부 확인
    //만료일이 오늘 이후이면 유효
    public boolean isValidMember(String plateNumber) throws SQLException {
        return monthlyParkingDAO.isValidMember(plateNumber);
    }

    // 검색 조건 + 정렬 포함 페이징 목록 조회
    // keyword 가 null 또는 빈 문자열이면 전체 조회와 동일하게 동작
    public List<MonthlyParkingDTO> getPagedListBySearch(int page, int pageSize, String keyword, String sortColumn, String sortOrder) throws SQLException {
        int offset = (page - 1) * pageSize;
        return monthlyParkingDAO.selectWithPagingAndSearch(offset, pageSize, keyword,
                sanitizeSortColumn(sortColumn), sanitizeSortOrder(sortOrder));
    }

    // 검색 조건 포함 전체 페이지 수 계산 (pageSize 동적 처리)
    public int getTotalPagesBySearch(String keyword, int pageSize) throws SQLException {
        int totalCount = monthlyParkingDAO.getTotalCountBySearch(keyword);
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    // 검색 조건 포함 전체 회원 수
    public int getTotalCountBySearch(String keyword) throws SQLException {
        return monthlyParkingDAO.getTotalCountBySearch(keyword);
    }

}
