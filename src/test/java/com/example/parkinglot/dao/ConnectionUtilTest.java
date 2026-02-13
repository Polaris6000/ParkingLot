package com.example.parkinglot.dao;

import com.example.parkinglot.util.ConnectionUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Log4j2
class ConnectionUtilTest {
    private ConnectionUtil connection;

    @BeforeEach
    public void ready(){
        connection = ConnectionUtil.INSTANCE;
    }

    @Test
    public void connectionTest() throws SQLException {
        log.info(connection.getConnection());
    }

}