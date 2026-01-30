package com.example.demo.repository;

import com.example.demo.Entity.RiskLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskLogRepository extends JpaRepository<RiskLog, Long> {
}
