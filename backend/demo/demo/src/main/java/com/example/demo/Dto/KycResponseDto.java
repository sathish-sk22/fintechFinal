package com.example.demo.Dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KycResponseDto {
    private String status;
    private String message;
}
