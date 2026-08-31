package com.tarot.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Hidden
public class TestController {

    private final ChatClient chatClient;

    @GetMapping("test-gemini")
    public ResponseEntity<String> testGemini() {
        String response = chatClient.prompt()
                .user("Hello, reply with 'Gemini OK' if connection works.")
                .call()
                .content();

        return ResponseEntity.ok(response);
    }
}
