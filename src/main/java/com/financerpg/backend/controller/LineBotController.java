package com.financerpg.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financerpg.backend.dto.TransactionRequest;
import com.financerpg.backend.service.LineService;
import com.financerpg.backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.financerpg.backend.service.LineLinkService;
import com.financerpg.backend.entity.User;

@RestController
@RequestMapping("/api/line")
@RequiredArgsConstructor
public class LineBotController {

    private final LineService lineService;
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;
    private final LineLinkService lineLinkService;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode events = root.get("events");

            for (JsonNode event : events) {
                String type = event.get("type").asText();
                if (!type.equals("message")) continue;

                String replyToken = event.get("replyToken").asText();
                String messageType = event.get("message").get("type").asText();
                if (!messageType.equals("text")) continue;

                String text = event.get("message").get("text").asText().trim();
                String userId = event.get("source").get("userId").asText();

                handleMessage(replyToken, userId, text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("OK");
    }

    private void handleMessage(String replyToken, String userId, String text) {
        if (text.toLowerCase().startsWith("link ")) {
            String code = text.substring(5).trim();
            boolean success = lineLinkService.linkLineUser(code, userId);
            if (success) {
                lineService.replyMessage(replyToken, "✅ เชื่อมบัญชีสำเร็จ! ตอนนี้พิมพ์รายการได้เลยครับ\nตัวอย่าง: กะเพรา 50");
            } else {
                lineService.replyMessage(replyToken, "❌ code ไม่ถูกต้องหรือหมดอายุแล้ว\nกรุณาขอ code ใหม่ที่เว็บไซต์");
            }
            return;
        }

        User user = lineLinkService.getUserByLineId(userId);
        if (user == null) {
            lineService.replyMessage(replyToken, "⚠️ ยังไม่ได้เชื่อมบัญชี\n1. เปิดเว็บแล้ว login\n2. ขอ code\n3. พิมพ์: link XXXXXX");
            return;
        }

        String[] lines = text.split("\n");
        StringBuilder reply = new StringBuilder();
        int count = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            ParsedTransaction parsed = parseLine(line);
            if (parsed == null) {
                reply.append("❌ อ่านไม่ออก: ").append(line).append("\n");
                continue;
            }

            try {
                TransactionRequest req = new TransactionRequest();
                req.setAmount(parsed.amount());
                req.setType(parsed.type());
                req.setNote(parsed.note());
                req.setTxDate(LocalDate.now());

                transactionService.addTransaction(user.getEmail(), req);

                String emoji = parsed.type().equals("EXPENSE") ? "💸" : "💰";
                reply.append(emoji).append(" ").append(parsed.note())
                        .append(" ").append(parsed.amount()).append(" บาท\n");
                count++;
            } catch (Exception e) {
                reply.append("❌ บันทึกไม่ได้: ").append(line).append("\n");
            }
        }

        if (count > 0) {
            reply.append("\n✅ บันทึกแล้ว ").append(count).append(" รายการ");
        }

        lineService.replyMessage(replyToken, reply.toString().trim());
    }

    private ParsedTransaction parseLine(String text) {
        try {
            String[] parts = text.split("\\s+");
            if (parts.length < 2) return null;

            String lastPart = parts[parts.length - 1]
                    .replace("บาท", "").replace(",", "").trim();
            BigDecimal amount = new BigDecimal(lastPart);

            StringBuilder noteBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                noteBuilder.append(parts[i]).append(" ");
            }
            String note = noteBuilder.toString().trim();

            String type = "EXPENSE";
            String[] incomeKeywords = {"รายรับ", "เงินเดือน", "โบนัส", "รายได้", "ได้รับ"};
            for (String keyword : incomeKeywords) {
                if (note.contains(keyword)) {
                    type = "INCOME";
                    break;
                }
            }

            return new ParsedTransaction(note, amount, type);
        } catch (Exception e) {
            return null;
        }
    }

    record ParsedTransaction(String note, BigDecimal amount, String type) {}
}