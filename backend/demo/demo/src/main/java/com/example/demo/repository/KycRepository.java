package com.example.demo.repository;

import com.example.demo.Entity.KycDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycRepository extends JpaRepository<KycDetails, Long> {

    Optional<KycDetails> findByUsername(String username);
}
