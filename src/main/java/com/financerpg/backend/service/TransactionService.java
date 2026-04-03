package com.financerpg.backend.service;

import com.financerpg.backend.dto.TransactionRequest;
import com.financerpg.backend.dto.TransactionResponse;
import com.financerpg.backend.entity.Transaction;
import com.financerpg.backend.entity.User;
import com.financerpg.backend.repository.TransactionRepository;
import com.financerpg.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionResponse addTransaction(String email, TransactionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .type(Transaction.TransactionType.valueOf(request.getType()))
                .note(request.getNote())
                .txDate(request.getTxDate() != null ? request.getTxDate() : LocalDate.now())
                .build();

        transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    public List<TransactionResponse> getMyTransactions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้"));

        return transactionRepository
                .findByUserIdOrderByTxDateDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteTransaction(String email, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("ไม่พบรายการ"));

        if (!transaction.getUser().getEmail().equals(email)) {
            throw new RuntimeException("ไม่มีสิทธิ์ลบรายการนี้");
        }

        transactionRepository.delete(transaction);
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getType().name(),
                t.getNote(),
                t.getTxDate(),
                t.getCreatedAt()
        );
    }
}