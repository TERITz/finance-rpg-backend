package com.financerpg.backend.controller;

import com.financerpg.backend.entity.Boss;
import com.financerpg.backend.service.BossService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/boss")
@RequiredArgsConstructor
public class BossController {

    private final BossService bossService;

    @PostMapping("/create")
    public ResponseEntity<Boss> createBoss(
            @AuthenticationPrincipal String email,
            @RequestBody Map<String, BigDecimal> body) {
        return ResponseEntity.ok(bossService.createBoss(email, body.get("savingGoal")));
    }

    @GetMapping("/current")
    public ResponseEntity<Boss> getCurrentBoss(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(bossService.getCurrentBoss(email));
    }

    @PostMapping("/attack")
    public ResponseEntity<Boss> attack(
            @AuthenticationPrincipal String email,
            @RequestBody Map<String, BigDecimal> body) {
        return ResponseEntity.ok(bossService.updateBossHp(email, body.get("amount")));
    }
}