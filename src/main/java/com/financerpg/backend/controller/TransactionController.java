package com.financerpg.backend.controller;

import com.financerpg.backend.dto.TransactionRequest;
import com.financerpg.backend.dto.TransactionResponse;
import com.financerpg.backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> add(
            @AuthenticationPrincipal String email,
            @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.addTransaction(email, request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(transactionService.getMyTransactions(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        transactionService.deleteTransaction(email, id);
        return ResponseEntity.noContent().build();
    }
}