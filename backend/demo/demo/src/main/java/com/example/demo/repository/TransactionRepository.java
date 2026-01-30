package com.example.demo.repository;

import com.example.demo.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromUserOrToUserOrderByCreatedAtDesc(
            String fromUser,
            String toUser
    );
    long countByFromUser(String fromUser);

    long countByFromUserAndCreatedAtAfter(
            String fromUser,
            LocalDateTime time
    );
}