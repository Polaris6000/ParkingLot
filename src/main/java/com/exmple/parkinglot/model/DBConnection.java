package com.exmple.parkinglot.model;

import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log4j2
public class DBConnection {

    // 데이터베이스 접속 정보 (상수로 관리하는 게 훨씬 깔끔)
    private static final String DRIVER = "org.mariadb.jdbc.Driver";
    private static final String DB_HOST = "127.0.0.1";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "parking_lot";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "6000";

    // JDBC URL 완성
    private static final String DB_URL = "jdbc:mariadb://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    // @return Connection 또는 실패 시 null
    public static Connection getConnection() {
        Connection connection = null;

        try {
            // 1) JDBC 드라이버 로딩
            Class.forName(DRIVER);

            // 2) Connection 객체 획득
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            if (connection != null) {
                log.info("!데이터베이스 연결 성공! (URL: {})", DB_URL);
            }

        } catch (ClassNotFoundException e) {
            log.error("JDBC 드라이버 로드 실패: {}", e.getMessage());
        } catch (SQLException e) {
            log.error("데이터베이스 연결 실패 (URL, ID, PW 확인 필요): {}", e.getMessage());
        }

        return connection;
    }
}