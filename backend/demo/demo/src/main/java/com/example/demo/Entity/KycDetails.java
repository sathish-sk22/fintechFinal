package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(nullable = false, length = 12)
    private String aadhaarNumber;

    @Column(nullable = false, length = 10)
    private String panNumber;

    @Column(nullable = false)
    private String dateOfBirth;

    @Column(nullable = false, length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    private KycStatus status;

    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
}
