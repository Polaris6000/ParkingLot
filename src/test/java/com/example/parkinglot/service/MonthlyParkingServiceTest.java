package com.example.parkinglot.service;

import com.example.parkinglot.dto.MonthlyParkingDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MonthlyParkingServiceTest {
    private final MonthlyParkingService monthlyParkingService = MonthlyParkingService.INSTANCE;

    //테스트용 차량번호 (테스트 후 삭제됨)
    private static final String TEST_PLATE = "999테999";
    private static int testId; //등록 후 Id 저장용

    @Test
    @Order(1)
    @DisplayName("회원 등록 - 정상 등록")
    void registerSuccess() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = MonthlyParkingDTO.builder()
                .plateNumber(TEST_PLATE)
                .name("테스트유저")
                .phoneNumber("010-1234-1234")
                .beginDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(1))
                .build();

        boolean result = monthlyParkingService.register(monthlyParkingDTO);
        assertTrue(result, "신규 회원 등록은 성공해야 합니다");
        log.info("등록성공:{}", TEST_PLATE);

        //등록된 회원 ID 저장(테스트에서 사용)
        MonthlyParkingDTO saved = monthlyParkingService.getByPlateNumber(TEST_PLATE);
        assertNotNull(saved);
        testId = saved.getId();
        log.info("등록된 ID : {}", testId);

    }

    @Test
    @Order(2)
    @DisplayName("회원 등록 - 차량 번호 중복 시 실패")
    void registerDuplicate() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = MonthlyParkingDTO.builder()
                .plateNumber(TEST_PLATE)
                .name("중복유저")
                .phoneNumber("010-1111-1112")
                .beginDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(1))
                .build();

        boolean result = monthlyParkingService.register(monthlyParkingDTO);
        assertFalse(result, "중복된 차량번호로 회원 등록은 실패해야 합니다");
        log.info("중복 등록 차단 확인 완료");
    }

    @Test
    @Order(3)
    @DisplayName("차량번호로 회원 조회")
    void getByPlateNumber() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingService.getByPlateNumber(TEST_PLATE);

        assertNotNull(monthlyParkingDTO, "등록된 차량번호로 조회 시 null이 아니어야 합니다");
        assertEquals(TEST_PLATE, monthlyParkingDTO.getPlateNumber());
        assertEquals("테스트유저", monthlyParkingDTO.getName());
        log.info("조회 결과 : {}", monthlyParkingDTO);
    }

    @Test
    @Order(4)
    @DisplayName("ID로 회원 조회")
    void getById() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingService.getById(testId);

        assertNotNull(monthlyParkingDTO);
        assertEquals(testId, monthlyParkingDTO.getId());
        assertEquals(TEST_PLATE, monthlyParkingDTO.getPlateNumber());
        log.info("ID로 조회 결과 : {}", monthlyParkingDTO);
    }

    @Test
    @Order(5)
    @DisplayName("회원 정보 수정 - 연락처 및 만료일 변경")
    void update() throws SQLException {
        LocalDate newExpiry = LocalDate.now().plusMonths(3);

        MonthlyParkingDTO monthlyParkingDTO = MonthlyParkingDTO.builder()
                .id(testId)
                .name("수정유저")
                .phoneNumber("010-1111-1113")
                .beginDate(LocalDate.now())
                .expiryDate(newExpiry)
                .build();

        boolean result = monthlyParkingService.update(monthlyParkingDTO);
        assertTrue(result, "회원 정보 수정은 성공해야 합니다");

        //수정 결과 확인
        MonthlyParkingDTO updatedDTO = monthlyParkingService.getById(testId);
        assertEquals("수정유저", updatedDTO.getName());
        assertEquals("010-1111-1113", updatedDTO.getPhoneNumber());
        assertEquals(newExpiry, updatedDTO.getExpiryDate());
        log.info("수정확인 : {}", updatedDTO);

    }

    @Test
    @Order(6)
    @DisplayName("월정액 유효 회원 확인 - 만료일 이후면 true")
    void isValidMember() throws SQLException {
        boolean valid = monthlyParkingService.isValidMember(TEST_PLATE);
        assertTrue(valid, "만료일 이후의 회원은 유효해야 합니다");
        log.info("유효 회원 확인 : {}", valid);
    }

    @Test
    @Order(7)
    @DisplayName("존재하지 않는 차량번호 조회 시 null반환")
    void getByPlateNumberNotFound() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingService.getByPlateNumber("00없000");
        assertNull(monthlyParkingDTO, "존재하지 않는 차량번호로 조회 시 null이어야 합니다");
    }

    @Test
    @Order(8)
    @DisplayName("페이징 목록 조회")
    void getPagedList() throws SQLException {
        List<MonthlyParkingDTO> list = monthlyParkingService.getPagedList(1);

        assertNotNull(list);
        assertTrue(list.size() <= monthlyParkingService.getPageSize(),
                "페이지 사이즈(" + monthlyParkingService.getPageSize() + ") 이하여야 합니다.");
        log.info("1페이지 조회 건수: {}", list.size());

    }

    @Test
    @Order(9)
    @DisplayName("전체 회원 수 및 페이지 수 조회")
    void getTotalCountAndPages() throws SQLException {
        int totalCount = monthlyParkingService.getTotalCount();
        int totalPages = monthlyParkingService.getTotalPages();

        assertTrue(totalCount >= 1, "데이터가 최소 1건 이상이어야함");
        assertTrue(totalPages >= 1);
        log.info("전체 회원 수: {} 명, 전체 페이지 수: {} 페이지", totalCount, totalPages);

    }

    @Test
    @Order(10)
    @DisplayName("전체 목록 조회")
    void getAllList() throws SQLException {
        List<MonthlyParkingDTO> list = monthlyParkingService.getAllList();

        assertNotNull(list);
        assertFalse(list.isEmpty(), "데이터가 최소 1건 이상이어야함");
        log.info("전체 목록 건수 : {}", list.size());
    }

    @Test
    @Order(11)
    @DisplayName("회원 삭제")
    void delete() throws SQLException {
        monthlyParkingService.delete(testId);

        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingService.getById(testId);
        assertNull(monthlyParkingDTO, "삭제 후 조회 시 null이어야 합니다.");
        log.info("삭제 완료 (id={})", testId);
    }

    @Test
    @Order(12)
    @DisplayName("삭제된 회원은 유효하지 않음")
    void isValidMemberAfterDelete() throws SQLException {
        boolean valid = monthlyParkingService.isValidMember(TEST_PLATE);
        assertFalse(valid, "삭제된 회원은 유효하지 않아야 합니다.");
        log.info("삭제 후 유효 검증 : {}", valid);
    }
}

//테스트 완료