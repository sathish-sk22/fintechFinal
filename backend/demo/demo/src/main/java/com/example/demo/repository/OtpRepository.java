package com.example.demo.repository;

import com.example.demo.model.OtpCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Transactional
public interface OtpRepository extends JpaRepository<OtpCode,Long> {

    Optional<OtpCode> findTopByUsernameAndUsedFalse(String username);
    void deleteByUsername(String email);


}
