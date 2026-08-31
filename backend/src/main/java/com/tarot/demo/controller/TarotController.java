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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tarot/")
@Tag(name = "Tarot API", description = "카드 3개 조회 및 타로 분석 API")
public class TarotController {
    private final TarotService tarotService;

    
    @GetMapping("3card")
    public ReadingResponseDTO select3Cards() {
        String readingId = UUID.randomUUID().toString();
        return tarotService.select3Cards(readingId);
    }

    @Operation(summary = "타로 결과 AI 분석 요청", description = "선택된 카드와 사용자 고민/카테고리를 바탕으로 타로 결과를 분석합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "분석 완료"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (readingId 누락 등)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류 또는 AI 서비스 연동 실패")
    })
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
