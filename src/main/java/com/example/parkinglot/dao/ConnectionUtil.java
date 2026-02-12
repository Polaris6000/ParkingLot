package com.example.parkinglot.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public enum ConnectionUtil {
    INSTANCE;

    private final HikariDataSource dataSource;

    ConnectionUtil(){
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver"); //db커넥션에 있는 클래스네임
        config.setJdbcUrl("jdbc:mariadb://localhost:3306/parking_lot");
        config.setUsername("admin");
        config.setPassword("8282");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);
    }
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
