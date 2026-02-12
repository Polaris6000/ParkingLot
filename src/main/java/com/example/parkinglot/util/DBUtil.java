package com.example.parkinglot.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 데이터베이스 연결 유틸리티 클래스
 */
public class DBUtil {
    
    // DB 연결 정보 (실제 환경에 맞게 수정 필요)
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/parking_lot";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "8282";
    
    // 드라이버 클래스명
    private static final String DRIVER_CLASS = "org.mariadb.jdbc.Driver";
    
    // static 블록에서 드라이버 로드
    static {
        try {
            Class.forName(DRIVER_CLASS);
            System.out.println("MariaDB 드라이버 로드 성공");
        } catch (ClassNotFoundException e) {
            System.err.println("MariaDB 드라이버를 찾을 수 없습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 데이터베이스 연결 객체 생성
     * @return Connection 객체
     * @throws SQLException DB 연결 실패 시
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("DB 연결 성공");
            return conn;
        } catch (SQLException e) {
            System.err.println("DB 연결 실패: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 데이터베이스 연결 종료
     * @param conn 닫을 Connection 객체
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("DB 연결 종료");
            } catch (SQLException e) {
                System.err.println("DB 연결 종료 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * DB 설정 정보 출력 (디버깅용)
     */
    public static void printDBInfo() {
        System.out.println("=== DB 연결 정보 ===");
        System.out.println("URL: " + DB_URL);
        System.out.println("USER: " + DB_USER);
        System.out.println("DRIVER: " + DRIVER_CLASS);
        System.out.println("==================");
    }
}
