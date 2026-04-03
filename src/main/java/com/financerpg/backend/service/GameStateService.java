package com.financerpg.backend.service;

import com.financerpg.backend.entity.GameState;
import com.financerpg.backend.entity.User;
import com.financerpg.backend.repository.GameStateRepository;
import com.financerpg.backend.repository.TransactionRepository;
import com.financerpg.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GameStateService {

    private final GameStateRepository gameStateRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public GameState getOrCreate(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้"));

        return gameStateRepository.findByUserId(user.getId())
                .orElseGet(() -> gameStateRepository.save(
                        GameState.builder().user(user).build()
                ));
    }

    public GameState updatePetMood(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้"));

        GameState gameState = gameStateRepository.findByUserId(user.getId())
                .orElseGet(() -> GameState.builder().user(user).build());

        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        BigDecimal income = transactionRepository
                .sumIncomeByMonth(user.getId(), month, year);
        BigDecimal expense = transactionRepository
                .sumExpenseByMonth(user.getId(), month, year);

        if (income.compareTo(BigDecimal.ZERO) == 0) {
            gameState.setPetMood(GameState.PetMood.NEUTRAL);
        } else {
            BigDecimal ratio = expense.divide(income, 4, RoundingMode.HALF_UP);
            if (ratio.compareTo(new BigDecimal("1.0")) > 0) {
                gameState.setPetMood(GameState.PetMood.SAD);
            } else if (ratio.compareTo(new BigDecimal("0.8")) > 0) {
                gameState.setPetMood(GameState.PetMood.NEUTRAL);
            } else {
                gameState.setPetMood(GameState.PetMood.HAPPY);
            }
        }

        return gameStateRepository.save(gameState);
    }

    public GameState addXp(String email, int xpToAdd) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้"));

        GameState gameState = gameStateRepository.findByUserId(user.getId())
                .orElseGet(() -> GameState.builder().user(user).build());

        gameState.setXp(gameState.getXp() + xpToAdd);
        gameState.setLevel(calculateLevel(gameState.getXp()));

        return gameStateRepository.save(gameState);
    }

    private int calculateLevel(int xp) {
        if (xp >= 5000) return 5;
        if (xp >= 3000) return 4;
        if (xp >= 1500) return 3;
        if (xp >= 500)  return 2;
        return 1;
    }
}