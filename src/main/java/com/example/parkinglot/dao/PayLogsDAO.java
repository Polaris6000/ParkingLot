package com.example.parkinglot.dao;


import com.example.parkinglot.util.ConnectionUtil;
import com.example.parkinglot.vo.PayLogsVO;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PayLogsDAO {
    public void insert(PayLogsVO payLogsVO) {
        String sql = "insert into pay_logs (id, pay_time, kind_of_discount, pay_log) VALUES" +
                " (?, ?, ?, ?)";
        try {
            //connection 연결해서 아이디 값 찾아오기
            @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, payLogsVO.getId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(payLogsVO.getPayTime()));
            preparedStatement.setString(3, payLogsVO.getKindOfDiscount());
            preparedStatement.setInt(4, payLogsVO.getPayLog());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
