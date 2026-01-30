package com.example.demo.Dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDto {

    private Long transactionId;
    private String fromUser;
    private String toUser;
    private BigDecimal amount;
    private String note;
    private String status;
    private LocalDateTime timestamp;
}

