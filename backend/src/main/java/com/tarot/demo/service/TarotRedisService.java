package com.tarot.demo.service;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tarot.demo.DTO.TarotCardDTO;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class TarotRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public TarotCardDTO getCard(String cardCode) {

        String key = "tarot:card:" + cardCode;

        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            throw new RuntimeException("카드를 찾을 수 없습니다. cardCode=" + cardCode);
        }

        try {
            return objectMapper.readValue(json, TarotCardDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("카드 데이터 변환에 실패했습니다.", e);
        }
    }
    
}
