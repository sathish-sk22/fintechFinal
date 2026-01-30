package com.example.demo.Util;

import com.example.demo.Entity.KycStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true ,nullable = false)
    private String username;
    @Column(unique = true ,nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    // 🔐 KYC fields
    private String aadhaar;
    private String pan;
    private LocalDate dob;
    private String address;

    // ✅ auto approve
    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus; // NOT_SUBMITTED, PENDING, APPROVED

    @Column(nullable = false)
    private boolean enabled = false;
}
