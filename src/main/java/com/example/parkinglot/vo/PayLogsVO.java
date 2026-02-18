package com.example.parkinglot.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayLogsVO {
    Integer id; //이거 외래키로 지정하면 될 듯?
    LocalDateTime payTime; // 결제 시간
    String kindOfDiscount; // 할인 종류
    Integer payLog; //지불 비용
}
