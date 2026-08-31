package com.tarot.demo.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tarot.demo.DTO.ReadingResponseDTO;
import com.tarot.demo.DTO.TarotAnalyzeDTO;
import com.tarot.demo.DTO.TarotCardDTO;
import com.tarot.demo.DTO.TarotReadingDTO;
import com.tarot.demo.config.TarotTools;
import com.tarot.demo.exception.CustomException;
import com.tarot.demo.exception.ErrorCode;
import com.tarot.demo.mapper.TarotMapper;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Service
public class TarotService {
    private final TarotMapper tarotMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final TarotTools tarotAnalysisTools;

    

    public ReadingResponseDTO select3Cards(String readingId) {
       List<TarotCardDTO> result = new ArrayList<>();

        // 1. Major에서 1장 랜덤
        String majorCode = redisTemplate
                .opsForSet()
                .randomMember("tarot:deck:major");

        if (majorCode == null) {
            throw new IllegalStateException("Major 카드가 없습니다.");
        }

        TarotCardDTO mainCard = getCard(majorCode);
        result.add(mainCard);


        // 2. Minor에서 2장 랜덤 (중복 없음)
        Set<String> minorCodes = redisTemplate
                .opsForSet()
                .distinctRandomMembers("tarot:deck:minor", 2);

        if (minorCodes == null || minorCodes.size() < 2) {
            throw new IllegalStateException(
                    "Minor 카드가 2장 이상 존재하지 않습니다."
            );
        }

        List<TarotCardDTO> minorCards = new ArrayList<>();

        for (String minorCode : minorCodes) {
            TarotCardDTO card = getCard(minorCode);

            result.add(card);
            minorCards.add(card);
        }


        // 3. readingId + 뽑힌 카드 저장
        TarotReadingDTO reading = new TarotReadingDTO();

        reading.setMainCardCode(mainCard.getCardCode());
        reading.setSub1CardCode(minorCards.get(0).getCardCode());
        reading.setSub2CardCode(minorCards.get(1).getCardCode());

        try {

            String json = objectMapper.writeValueAsString(reading);

            redisTemplate.opsForValue().set(
                    "tarot:reading:" + readingId,
                    json,
                    Duration.ofMinutes(30)
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "타로 리딩 정보를 저장하지 못했습니다.", e
            );
        }


        // 4. 프론트에는 readingId + 카드 정보 반환
        return new ReadingResponseDTO(readingId, result);
    }

    // Redis에서 카드 상세정보 조회
    private TarotCardDTO getCard(String cardCode) {

        String key = "tarot:card:" + cardCode;

        String json = redisTemplate
                .opsForValue()
                .get(key);

        if (json == null) {
            throw new IllegalStateException(
                    "Redis에 카드 정보가 없습니다: " + cardCode
            );
        }

        return objectMapper.readValue(json, TarotCardDTO.class);
    }

    public String analyze(
            String readingId,
            String category,
            String concern) {

        // 1. readingId로 Redis에서 카드 3장 정보 조회
        TarotReadingDTO reading = getReading(readingId);

        // 2. AI에게 전달할 카드 코드
        String mainCard = reading.getMainCardCode();
        String sub1Card = reading.getSub1CardCode();
        String sub2Card = reading.getSub2CardCode();


       try {

        // 3. AI Agent 호출
        return chatClient.prompt()

                .system("""
                    당신은 전문 타로 AI 상담사입니다.

                    사용자의 질문과 선택한 운의 종류를 분석하고,
                    질문의 세부 의도를 파악한 뒤 적절한 분석 Tool을 사용하세요.

                    반드시 사용자의 질문과 카드의 의미를 연결해서 해석하세요.

                    카드의 키워드나 의미를 단순히 나열하지 마세요.

                    세 장의 카드는 다음 역할을 가집니다.

                    첫 번째 카드:
                    현재 상황

                    두 번째 카드:
                    현재 상황에 영향을 주는 요소

                    세 번째 카드:
                    앞으로의 흐름

                    최종 답변은 사용자가 이해하기 쉬운 자연스러운
                    한국어로 작성하세요.

                    타로는 미래를 확정적으로 예언하는 것이 아니라
                    현재 상황을 돌아보고 참고할 수 있는 해석으로 제공하세요.
                    """)

                .user("""
                    운의 종류: %s

                    사용자의 고민:
                    %s

                    현재 카드 코드: %s
                    영향 카드 코드: %s
                    미래 카드 코드: %s

                    위 정보를 바탕으로 사용자의 질문 의도를 파악하고
                    적절한 Tarot 분석 Tool을 사용하세요.

                    Tool에서 카드의 상세 정보와 의미를 조회한 뒤,
                    그 결과를 바탕으로 최종 해석을 작성하세요.
                    """.formatted(
                        category,
                        concern,
                        mainCard,
                        sub1Card,
                        sub2Card
                ))

                .tools(tarotAnalysisTools)

                .call()

                .content();

        } catch (RuntimeException e) {

            Throwable cause = e;

        while (cause != null) {

            if (cause instanceof com.google.genai.errors.ClientException clientException) {

                if (clientException.getMessage() != null && clientException.getMessage().contains("429")) {
                    throw new CustomException(
                            ErrorCode.AI_QUOTA_EXCEEDED
                    );
                }
            }

            cause = cause.getCause();
            }

            // 429가 아닌 다른 오류는 그대로 전달
            throw e;
        }
    }
    


    private TarotReadingDTO getReading(String readingId) {

    String key = "tarot:reading:" + readingId;

    String json = redisTemplate
            .opsForValue()
            .get(key);

    if (json == null) {
        throw new IllegalStateException(
                "타로 리딩 정보를 찾을 수 없습니다: " + readingId
        );
    }

    try {
        return objectMapper.readValue(
                json,
                TarotReadingDTO.class
        );

    } catch (Exception e) {
        throw new IllegalStateException(
                "타로 리딩 정보를 읽을 수 없습니다.",
                e
        );
    }
}
}
