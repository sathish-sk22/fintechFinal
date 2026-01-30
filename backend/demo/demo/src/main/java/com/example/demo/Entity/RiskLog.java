package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "risk_logs")
public class RiskLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String riskType;     // HIGH_AMOUNT, VELOCITY, FIRST_TIME

    private String severity;     // LOW, MEDIUM, HIGH

    private String description;

    private LocalDateTime detectedAt;
}

