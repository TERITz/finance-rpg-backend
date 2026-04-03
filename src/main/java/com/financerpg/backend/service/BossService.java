package com.financerpg.backend.service;

import com.financerpg.backend.entity.Boss;
import com.financerpg.backend.entity.Budget;
import com.financerpg.backend.entity.User;
import com.financerpg.backend.repository.BossRepository;
import com.financerpg.backend.repository.BudgetRepository;
import com.financerpg.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BossService {

    private final BossRepository bossRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public Boss createBoss(String email, BigDecimal savingGoal) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้"));

        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        Budget budget = budgetRepository
                .findByUserIdAndMonthAndYear(user.getId(), month, year)
                .orElseGet(() -> budgetRepository.save(
                        Budget.builder()
                                .user(user)
                                .month(month)
                                .year(year)
                                .savingGoal(savingGoal)
                                .build()
                ));

        int winCount = bossRepository.countByUserIdAndStatus(user.getId(), Boss.BossStatus.WIN);
        int level = calculateLevel(winCount);

        Boss boss = Boss.builder()
                .user(user)
                .budget(budget)
                .targetAmount(savingGoal)
                .level(level)
                .build();

        return bossRepository.save(boss);
    }

    public Boss getCurrentBoss(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้"));

        return bossRepository
                .findByUserIdAndStatus(user.getId(), Boss.BossStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("ไม่มี Boss ที่กำลังต่อสู้อยู่"));
    }

    public Boss updateBossHp(String email, BigDecimal savedAmount) {
        Boss boss = getCurrentBoss(email);

        BigDecimal newSaved = boss.getCurrentSaved().add(savedAmount);
        boss.setCurrentSaved(newSaved);

        int newHp = calculateHp(newSaved, boss.getTargetAmount());
        boss.setHpCurrent(newHp);

        if (newHp <= 0) {
            boss.setHpCurrent(0);
            boss.setStatus(Boss.BossStatus.WIN);
        }

        return bossRepository.save(boss);
    }

    private int calculateHp(BigDecimal saved, BigDecimal target) {
        if (target.compareTo(BigDecimal.ZERO) == 0) return 0;
        BigDecimal ratio = saved.divide(target, 4, RoundingMode.HALF_UP);
        int hp = 100 - ratio.multiply(BigDecimal.valueOf(100)).intValue();
        return Math.max(0, hp);
    }

    private int calculateLevel(int winCount) {
        if (winCount >= 5) return 4;
        if (winCount >= 3) return 3;
        if (winCount >= 1) return 2;
        return 1;
    }
}