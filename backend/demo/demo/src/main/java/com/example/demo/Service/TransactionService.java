package com.example.demo.Service;

import com.example.demo.Dto.TransactionDto;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionDto> getMyTransactions(String username) {

        return transactionRepository
                .findByFromUserOrToUserOrderByCreatedAtDesc(username, username)
                .stream()
                .map(tx -> TransactionDto.builder()
                        .transactionId(tx.getId())
                        .fromUser(tx.getFromUser())
                        .toUser(tx.getToUser())
                        .amount(tx.getAmount())
                        .note(tx.getNote())
                        .status(tx.getStatus().name())
                        .timestamp(tx.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
