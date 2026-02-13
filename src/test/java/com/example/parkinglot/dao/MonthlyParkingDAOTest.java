package com.example.parkinglot.dao;

import com.example.parkinglot.dto.MonthlyParkingDTO;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MonthlyParkingDAOTest {

    private static MonthlyParkingDAO monthlyParkingDAO;
    private static int testId;
    private static final String TEST_PLATE = "99테9999";

    @BeforeAll
    static void setUp() {
        monthlyParkingDAO = new MonthlyParkingDAO();
    }

    @Test
    @Order(0)
    @DisplayName("정리 - 이전 테스트 데이터 제거")
    void cleanUp() throws SQLException {
        MonthlyParkingDTO existing = monthlyParkingDAO.selectByPlateNumber(TEST_PLATE);
        if (existing != null) {
            monthlyParkingDAO.delete(existing.getId());
        }
        assertNull(monthlyParkingDAO.selectByPlateNumber(TEST_PLATE));
    }

    @Test
    @Order(1)
    @DisplayName("회원 등록 - 정상 입력")
    void insert() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = MonthlyParkingDTO.builder()
                .plateNumber(TEST_PLATE)
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .beginDate(LocalDate.of(2025, 7, 1))
                .expiryDate(LocalDate.of(2025, 7, 31))
                .build();

        assertDoesNotThrow(() -> monthlyParkingDAO.insert(monthlyParkingDTO));

        MonthlyParkingDTO saved = monthlyParkingDAO.selectByPlateNumber(TEST_PLATE);
        assertNotNull(saved, "삽입된 회원이 조회되어야 합니다.");
        assertEquals("홍길동", saved.getName());
        assertEquals("010-1234-5678", saved.getPhoneNumber());

        testId = saved.getId();
        System.out.println("생성된 테스트 ID: " + testId);
    }

    @Test
    @Order(2)
    @DisplayName("회원 등록 - 중복 차량번호 예외 발생")
    void insertDuplicate() {
        MonthlyParkingDTO duplicate = MonthlyParkingDTO.builder()
                .plateNumber(TEST_PLATE)  // 이미 등록된 차량번호
                .name("김철수")
                .phoneNumber("010-9999-8888")
                .beginDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(1))
                .build();

        assertThrows(SQLException.class, () -> monthlyParkingDAO.insert(duplicate),
                "중복 차량번호 등록 시 SQLException이 발생해야 합니다.");
    }

    @Test
    @Order(3)
    @DisplayName("ID로 회원 조회")
    void selectById() throws SQLException {
        MonthlyParkingDTO result = monthlyParkingDAO.selectById(testId);

        assertNotNull(result, "등록된 ID로 조회 시 결과가 있어야 합니다.");
        assertEquals(testId, result.getId());
        assertEquals(TEST_PLATE, result.getPlateNumber());
        assertEquals("홍길동", result.getName());
    }

    @Test
    @Order(4)
    @DisplayName("존재하지 않는 ID로 조회 시 null 반환")
    void selectByIdNotFound() throws SQLException {
        MonthlyParkingDTO result = monthlyParkingDAO.selectById(-1);
        assertNull(result, "존재하지 않는 ID 조회 시 null이어야 합니다.");
    }

    @Test
    @Order(5)
    @DisplayName("차량번호로 회원 조회")
    void selectByPlateNumber() throws SQLException {
        MonthlyParkingDTO result = monthlyParkingDAO.selectByPlateNumber(TEST_PLATE);

        assertNotNull(result);
        assertEquals(TEST_PLATE, result.getPlateNumber());
        assertEquals("홍길동", result.getName());
    }

    @Test
    @Order(6)
    @DisplayName("존재하지 않는 차량번호 조회 시 null 반환")
    void selectByPlateNumberNotFound() throws SQLException {
        MonthlyParkingDTO result = monthlyParkingDAO.selectByPlateNumber("00없0000");
        assertNull(result, "존재하지 않는 차량번호 조회 시 null이어야 합니다.");
    }

    @Test
    @Order(7)
    @DisplayName("전체 회원 목록 조회")
    void selectAll() throws SQLException {
        List<MonthlyParkingDTO> list = monthlyParkingDAO.selectAll();

        assertNotNull(list);
        assertFalse(list.isEmpty(), "최소 1건 이상의 데이터가 있어야 합니다.");

        boolean found = list.stream()
                .anyMatch(dto -> TEST_PLATE.equals(dto.getPlateNumber()));
        assertTrue(found, "테스트 차량번호가 전체 목록에 포함되어야 합니다.");
    }


    @Test
    @Order(8)
    @DisplayName("페이징 조회 - 첫 페이지")
    void selectWithPaging() throws SQLException {
        int pageSize = 5;
        List<MonthlyParkingDTO> page1 = monthlyParkingDAO.selectWithPaging(0, pageSize);

        assertNotNull(page1);
        assertTrue(page1.size() <= pageSize,
                "페이지 크기(" + pageSize + ") 이하의 결과가 반환되어야 합니다.");
    }

    @Test
    @Order(9)
    @DisplayName("전체 회원 수 조회")
    void getTotalCount() throws SQLException {
        int totalCount = monthlyParkingDAO.getTotalCount();

        assertTrue(totalCount >= 1, "최소 1명 이상의 회원이 있어야 합니다.");
        System.out.println("전체 회원 수: " + totalCount);
    }

    @Test
    @Order(10)
    @DisplayName("페이징 - 전체 수와 페이지 합계 일치 확인")
    void pagingConsistency() throws SQLException {
        int totalCount = monthlyParkingDAO.getTotalCount();
        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        int sumFromPages = 0;

        for (int i = 0; i < totalPages; i++) {
            List<MonthlyParkingDTO> page = monthlyParkingDAO.selectWithPaging(i * pageSize, pageSize);
            sumFromPages += page.size();
        }

        assertEquals(totalCount, sumFromPages,
                "모든 페이지의 데이터 합이 전체 수와 일치해야 합니다.");
    }

    @Test
    @Order(11)
    @DisplayName("유효한 월정액 회원 확인 - 만료일이 미래")
    void isValidMemberActive() throws SQLException {
        // 먼저 만료일을 미래로 업데이트
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingDAO.selectById(testId);
        assertNotNull(monthlyParkingDTO);

        monthlyParkingDTO.setExpiryDate(LocalDate.now().plusMonths(1));
        monthlyParkingDAO.update(monthlyParkingDTO);

        boolean isValid = monthlyParkingDAO.isValidMember(TEST_PLATE);
        assertTrue(isValid, "만료일이 미래인 회원은 유효해야 합니다.");
    }

    @Test
    @Order(12)
    @DisplayName("만료된 월정액 회원 확인 - 만료일이 과거")
    void isValidMemberExpired() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingDAO.selectById(testId);
        assertNotNull(monthlyParkingDTO);

        monthlyParkingDTO.setExpiryDate(LocalDate.of(2020, 1, 1));
        monthlyParkingDAO.update(monthlyParkingDTO);

        boolean isValid = monthlyParkingDAO.isValidMember(TEST_PLATE);
        assertFalse(isValid, "만료일이 과거인 회원은 유효하지 않아야 합니다.");
    }

    @Test
    @Order(13)
    @DisplayName("등록되지 않은 차량의 회원 유효성 확인")
    void isValidMemberNotRegistered() throws SQLException {
        boolean isValid = monthlyParkingDAO.isValidMember("00없0000");
        assertFalse(isValid, "등록되지 않은 차량은 유효하지 않아야 합니다.");
    }

    @Test
    @Order(14)
    @DisplayName("회원 정보 수정 - 연락처, 만료일 변경")
    void update() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = monthlyParkingDAO.selectById(testId);
        assertNotNull(monthlyParkingDTO);

        String newPhone = "010-5555-6666";
        LocalDate newExpiry = LocalDate.of(2025, 12, 31);
        monthlyParkingDTO.setPhoneNumber(newPhone);
        monthlyParkingDTO.setExpiryDate(newExpiry);

        boolean result = monthlyParkingDAO.update(monthlyParkingDTO);
        assertTrue(result, "수정이 성공해야 합니다.");

        MonthlyParkingDTO updated = monthlyParkingDAO.selectById(testId);
        assertNotNull(updated);
        assertEquals(newPhone, updated.getPhoneNumber(), "연락처가 변경되어야 합니다.");
        assertEquals(newExpiry, updated.getExpiryDate(), "만료일이 변경되어야 합니다.");
    }

    @Test
    @Order(15)
    @DisplayName("존재하지 않는 회원 수정 시 false 반환")
    void updateNotFound() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = MonthlyParkingDTO.builder()
                .id(-1)
                .name("없는사람")
                .phoneNumber("010-0000-0000")
                .beginDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(1))
                .build();

        boolean result = monthlyParkingDAO.update(monthlyParkingDTO);
        assertFalse(result, "존재하지 않는 ID 수정 시 false를 반환해야 합니다.");
    }

    @Test
    @Order(16)
    @DisplayName("회원 삭제")
    void delete() throws SQLException {
        assertDoesNotThrow(() -> monthlyParkingDAO.delete(testId));

        MonthlyParkingDTO deleted = monthlyParkingDAO.selectById(testId);
        assertNull(deleted, "삭제 후 조회 시 null이어야 합니다.");
    }

    @Test
    @Order(17)
    @DisplayName("삭제 후 차량번호 재등록 가능 확인")
    void reInsertAfterDelete() throws SQLException {
        MonthlyParkingDTO monthlyParkingDTO = MonthlyParkingDTO.builder()
                .plateNumber(TEST_PLATE)
                .name("재등록자")
                .phoneNumber("010-7777-8888")
                .beginDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(1))
                .build();

        assertDoesNotThrow(() -> monthlyParkingDAO.insert(monthlyParkingDTO));

        MonthlyParkingDTO saved = monthlyParkingDAO.selectByPlateNumber(TEST_PLATE);
        assertNotNull(saved);
        assertEquals("재등록자", saved.getName());

        monthlyParkingDAO.delete(saved.getId());
    }
}