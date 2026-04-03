package com.financerpg.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    private BigDecimal amount;
    private String type;
    private String note;
    private LocalDate txDate;
}