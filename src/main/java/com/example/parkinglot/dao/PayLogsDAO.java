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
    // SQL에서 id와 첫 번째 '?'를 제거
    String sql = "insert into pay_logs (pay_time, kind_of_discount, pay_log) VALUES (?, ?, ?)";

    try {
        @Cleanup Connection connection = ConnectionUtil.INSTANCE.getConnection();
        @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(sql);

        // id 세팅은 지워버리고 순서를 1번부터 다시 맞춤
        preparedStatement.setTimestamp(1, Timestamp.valueOf(payLogsVO.getPayTime()));
        preparedStatement.setString(2, payLogsVO.getKindOfDiscount());
        preparedStatement.setInt(3, payLogsVO.getPayLog());

        preparedStatement.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}
}
