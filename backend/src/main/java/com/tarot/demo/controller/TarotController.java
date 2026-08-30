package com.tarot.demo.controller;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tarot.demo.DTO.ReadingResponseDTO;
import com.tarot.demo.DTO.TarotAnalyzeDTO;
import com.tarot.demo.DTO.TarotCardDTO;
import com.tarot.demo.service.TarotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tarot/")
public class TarotController {
    private final TarotService tarotService;

    @GetMapping("card")
    public List<TarotCardDTO> selectAllCards() {
        return tarotService.selectAllCards();
    }

    @GetMapping("3card")
    public ReadingResponseDTO select3Cards() {
        String readingId = UUID.randomUUID().toString();
        return tarotService.select3Cards(readingId);
    }

    @PostMapping("analyze")
    public ResponseEntity<String> analyzeReading(@RequestBody TarotAnalyzeDTO analyzeDTO) {
        String response = tarotService.analyze(
                analyzeDTO.getReadingId(),
                analyzeDTO.getCategory(),
                analyzeDTO.getConcern()
        );
        return ResponseEntity.ok(response);
    }
}
