package com.financerpg.backend.controller;

import com.financerpg.backend.service.LineLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/line")
@RequiredArgsConstructor
public class LineLinkController {

    private final LineLinkService lineLinkService;

    @PostMapping("/generate-code")
    public ResponseEntity<Map<String, String>> generateCode(
            @AuthenticationPrincipal String email) {
        String code = lineLinkService.generateLinkCode(email);
        return ResponseEntity.ok(Map.of(
                "code", code,
                "message", "พิมพ์ใน LINE ว่า: link " + code
        ));
    }
}