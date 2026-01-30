package com.example.demo.Dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class KycRequestDto {

    @NotBlank
    @Pattern(regexp = "\\d{12}", message = "Aadhaar must be 12 digits")
    private String aadhaarNumber;

    @NotBlank
    @Pattern(
            regexp = "[A-Z]{5}[0-9]{4}[A-Z]",
            message = "Invalid PAN format"
    )
    private String panNumber;

    @NotBlank
    private String dateOfBirth;

    @NotBlank
    private String address;
}
