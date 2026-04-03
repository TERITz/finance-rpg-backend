package com.financerpg.backend.repository;

import com.financerpg.backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByTxDateDesc(Long userId);

    List<Transaction> findByUserIdAndTypeOrderByTxDateDesc(
            Long userId, Transaction.TransactionType type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = 'INCOME' " +
            "AND MONTH(t.txDate) = :month AND YEAR(t.txDate) = :year")
    BigDecimal sumIncomeByMonth(Long userId, int month, int year);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = 'EXPENSE' " +
            "AND MONTH(t.txDate) = :month AND YEAR(t.txDate) = :year")
    BigDecimal sumExpenseByMonth(Long userId, int month, int year);
}