package com.financerpg.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String type;
    private String note;
    private LocalDate txDate;
    private LocalDateTime createdAt;
}