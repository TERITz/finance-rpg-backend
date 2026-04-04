package com.financerpg.backend.service;

import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LineService {

    @Value("${line.bot.channel-token}")
    private String channelToken;

    private final OkHttpClient httpClient = new OkHttpClient();

    public void replyMessage(String replyToken, String message) {
        String safeMessage = message
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");

        String json = "{\"replyToken\":\"" + replyToken + "\",\"messages\":[{\"type\":\"text\",\"text\":\"" + safeMessage + "\"}]}";

        Request request = new Request.Builder()
                .url("https://api.line.me/v2/bot/message/reply")
                .addHeader("Authorization", "Bearer " + channelToken)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("LINE API Error: " + response.code());
                System.out.println("Body: " + response.body().string());
            } else {
                System.out.println("LINE reply success");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}