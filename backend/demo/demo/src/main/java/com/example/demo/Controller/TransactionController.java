package com.example.demo.Controller;

import com.example.demo.Dto.TransactionDto;
import com.example.demo.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/my")
    public ResponseEntity<List<TransactionDto>> myTransactions(
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(
                transactionService.getMyTransactions(username)
        );
    }
}