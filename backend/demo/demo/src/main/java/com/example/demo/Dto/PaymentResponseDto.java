package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponseDto {

    private Long transactionId;
    private String status;
    private BigDecimal amount;
    private String message;
    private LocalDateTime timestamp;
}
