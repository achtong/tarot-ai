package com.tarot.demo.tarot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tarot.demo.tarot.dto.TarotCardDTO;
import com.tarot.demo.tarot.mapper.TarotMapper;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Service
public class TarotService {
    private final TarotMapper tarotMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public List<TarotCardDTO> selectAllCards() {
        return tarotMapper.selectAllCards();
    }

    public List<TarotCardDTO> select3Cards() {
        List<TarotCardDTO> result = new ArrayList<>();

        // 1. Major에서 1장 랜덤
        String majorCode = redisTemplate
                .opsForSet()
                .randomMember("tarot:deck:major");

        if (majorCode == null) {
            throw new IllegalStateException("Major 카드가 없습니다.");
        }

        result.add(getCard(majorCode));


        // 2. Minor에서 2장 랜덤 (중복 없음)
        Set<String> minorCodes = redisTemplate
                .opsForSet()
                .distinctRandomMembers("tarot:deck:minor", 2);

        if (minorCodes == null || minorCodes.size() < 2) {
            throw new IllegalStateException("Minor 카드가 2장 이상 존재하지 않습니다.");
        }

        for (String minorCode : minorCodes) {
            result.add(getCard(minorCode));
        }

        return result;
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
}
