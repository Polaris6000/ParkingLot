package com.example.parkinglot.dao;

import com.example.parkinglot.dto.SettingDTO;
import com.example.parkinglot.util.ConnectionUtil;
import com.example.parkinglot.vo.SettingVO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 요금 정책 데이터 액세스 객체 (Data Access Object)
 * 데이터베이스와 직접 상호작용하는 계층
 */
public class SettingDAO {

    /**
     * 새로운 요금 정책 등록 (INSERT)
     * @param dto 등록할 요금 정책 정보
     * @return 등록 성공 여부
     * @throws SQLException
     */
    public boolean insertFeePolicy(SettingDTO dto) throws SQLException {
        String sql = "INSERT INTO fee_policy (base_fee, basic_unit_minute, unit_fee, " +
                "billing_unit_minutes, help_discount_rate, compact_discount_rate, " +
                "grace_period_minutes, max_cap_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, dto.getBaseFee());
            preparedStatement.setInt(2, dto.getBasicUnitMinute());
            preparedStatement.setInt(3, dto.getUnitFee());
            preparedStatement.setInt(4, dto.getBillingUnitMinutes());
            preparedStatement.setInt(5, dto.getHelpDiscountRate());
            preparedStatement.setInt(6, dto.getCompactDiscountRate());
            preparedStatement.setInt(7, dto.getGracePeriodMinutes());
            preparedStatement.setInt(8, dto.getMaxCapAmount());

            int result = preparedStatement.executeUpdate();
            System.out.println("DAO - 요금 정책 등록: " + (result > 0 ? "성공" : "실패"));
            return result > 0;
        } catch (SQLException e) {
            System.err.println("DAO - 요금 정책 등록 실패: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 전체 요금 정책 목록 조회 (최신순 정렬)
     * @return 요금 정책 목록
     * @throws SQLException
     */
    public List<SettingVO> selectAllFeePolicies() throws SQLException {
        String sql = "SELECT id, base_fee, basic_unit_minute, unit_fee, " +
                "billing_unit_minutes, help_discount_rate, compact_discount_rate, " +
                "grace_period_minutes, max_cap_amount, update_date " +
                "FROM fee_policy " +
                "ORDER BY id DESC";

        List<SettingVO> list = new ArrayList<>();

        System.out.println("===== DAO selectAllFeePolicies 실행 =====");
        System.out.println("SQL: " + sql);

        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("DB 연결 성공");

            int count = 0;
            while (resultSet.next()) {
                count++;
                SettingVO vo = new SettingVO();
                vo.setId(resultSet.getInt("id"));
                vo.setBaseFee(resultSet.getInt("base_fee"));
                vo.setBasicUnitMinute(resultSet.getInt("basic_unit_minute"));
                vo.setUnitFee(resultSet.getInt("unit_fee"));
                vo.setBillingUnitMinutes(resultSet.getInt("billing_unit_minutes"));
                vo.setHelpDiscountRate(resultSet.getInt("help_discount_rate"));
                vo.setCompactDiscountRate(resultSet.getInt("compact_discount_rate"));
                vo.setGracePeriodMinutes(resultSet.getInt("grace_period_minutes"));
                vo.setMaxCapAmount(resultSet.getInt("max_cap_amount"));

                Timestamp timestamp = resultSet.getTimestamp("update_date");
                if (timestamp != null) {
                    vo.setUpdateDate(timestamp.toLocalDateTime());
                }

                list.add(vo);

                System.out.println("조회된 데이터 " + count + ": ID=" + vo.getId() +
                        ", 기본요금=" + vo.getBaseFee() +
                        ", 날짜=" + vo.getUpdateDate());
            }

            System.out.println("총 조회 건수: " + list.size());
            System.out.println("========================================");
        } catch (SQLException e) {
            System.err.println("DAO - 요금 정책 목록 조회 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return list;
    }

    /**
     * 최신 요금 정책 조회 (현재 적용 중인 정책)
     * @return 최신 요금 정책
     * @throws SQLException
     */
    public SettingVO selectLatestFeePolicy() throws SQLException {
        String sql = "SELECT id, base_fee, basic_unit_minute, unit_fee, " +
                "billing_unit_minutes, help_discount_rate, compact_discount_rate, " +
                "grace_period_minutes, max_cap_amount, update_date " +
                "FROM fee_policy " +
                "ORDER BY id DESC " +
                "LIMIT 1";

        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                SettingVO vo = new SettingVO();
                vo.setId(resultSet.getInt("id"));
                vo.setBaseFee(resultSet.getInt("base_fee"));
                vo.setBasicUnitMinute(resultSet.getInt("basic_unit_minute"));
                vo.setUnitFee(resultSet.getInt("unit_fee"));
                vo.setBillingUnitMinutes(resultSet.getInt("billing_unit_minutes"));
                vo.setHelpDiscountRate(resultSet.getInt("help_discount_rate"));
                vo.setCompactDiscountRate(resultSet.getInt("compact_discount_rate"));
                vo.setGracePeriodMinutes(resultSet.getInt("grace_period_minutes"));
                vo.setMaxCapAmount(resultSet.getInt("max_cap_amount"));

                Timestamp timestamp = resultSet.getTimestamp("update_date");
                if (timestamp != null) {
                    vo.setUpdateDate(timestamp.toLocalDateTime());
                }

                System.out.println("DAO - 최신 요금 정책 조회 완료: ID=" + vo.getId());
                return vo;
            }
        } catch (SQLException e) {
            System.err.println("DAO - 최신 요금 정책 조회 실패: " + e.getMessage());
            throw e;
        }

        return null;
    }

    /**
     * 특정 ID의 요금 정책 조회
     * @param id 요금 정책 ID
     * @return 해당 ID의 요금 정책
     * @throws SQLException
     */
    public SettingVO selectFeePolicyById(int id) throws SQLException {
        String sql = "SELECT id, base_fee, basic_unit_minute, unit_fee, " +
                "billing_unit_minutes, help_discount_rate, compact_discount_rate, " +
                "grace_period_minutes, max_cap_amount, update_date " +
                "FROM fee_policy " +
                "WHERE id = ?";

        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    SettingVO vo = new SettingVO();
                    vo.setId(resultSet.getInt("id"));
                    vo.setBaseFee(resultSet.getInt("base_fee"));
                    vo.setBasicUnitMinute(resultSet.getInt("basic_unit_minute"));
                    vo.setUnitFee(resultSet.getInt("unit_fee"));
                    vo.setBillingUnitMinutes(resultSet.getInt("billing_unit_minutes"));
                    vo.setHelpDiscountRate(resultSet.getInt("help_discount_rate"));
                    vo.setCompactDiscountRate(resultSet.getInt("compact_discount_rate"));
                    vo.setGracePeriodMinutes(resultSet.getInt("grace_period_minutes"));
                    vo.setMaxCapAmount(resultSet.getInt("max_cap_amount"));

                    Timestamp timestamp = resultSet.getTimestamp("update_date");
                    if (timestamp != null) {
                        vo.setUpdateDate(timestamp.toLocalDateTime());
                    }

                    System.out.println("DAO - ID별 요금 정책 조회 완료: " + id);
                    return vo;
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO - ID별 요금 정책 조회 실패: " + e.getMessage());
            throw e;
        }

        return null;
    }

    /**
     * 요금 정책 개수 조회
     * @return 등록된 요금 정책 개수
     * @throws SQLException
     */
    public int countFeePolicies() throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM fee_policy";

        try (Connection conn = ConnectionUtil.INSTANCE.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                int count = resultSet.getInt("cnt");
                System.out.println("DAO - 요금 정책 개수: " + count);
                return count;
            }
        } catch (SQLException e) {
            System.err.println("DAO - 요금 정책 개수 조회 실패: " + e.getMessage());
            throw e;
        }

        return 0;
    }
}
