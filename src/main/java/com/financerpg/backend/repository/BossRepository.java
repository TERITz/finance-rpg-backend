package com.financerpg.backend.repository;

import com.financerpg.backend.entity.Boss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BossRepository extends JpaRepository<Boss, Long> {
    Optional<Boss> findByUserIdAndStatus(Long userId, Boss.BossStatus status);
    int countByUserIdAndStatus(Long userId, Boss.BossStatus status);
}