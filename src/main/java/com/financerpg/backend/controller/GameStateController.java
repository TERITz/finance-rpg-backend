package com.financerpg.backend.controller;

import com.financerpg.backend.entity.GameState;
import com.financerpg.backend.service.GameStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameStateController {

    private final GameStateService gameStateService;

    @GetMapping("/state")
    public ResponseEntity<GameState> getState(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(gameStateService.getOrCreate(email));
    }

    @PostMapping("/pet/update")
    public ResponseEntity<GameState> updatePetMood(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(gameStateService.updatePetMood(email));
    }

    @PostMapping("/xp/add")
    public ResponseEntity<GameState> addXp(
            @AuthenticationPrincipal String email,
            @RequestParam int amount) {
        return ResponseEntity.ok(gameStateService.addXp(email, amount));
    }
}