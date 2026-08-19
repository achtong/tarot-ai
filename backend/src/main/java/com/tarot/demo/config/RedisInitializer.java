package com.tarot.demo.config;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.mapper.TestMapper;
import com.tarot.demo.tarot.dto.TarotCardDTO;
import com.tarot.demo.tarot.mapper.TarotMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RedisInitializer {
    private final TestMapper testMapper;
    private final TarotMapper tarotCardMapper;
    private final StringRedisTemplate redisTemplate;
    
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        // 타로 관련
        List<TarotCardDTO> cards = tarotCardMapper.selectAllCards();

        // 기존 덱 초기화
        redisTemplate.delete("tarot:deck:major");
        redisTemplate.delete("tarot:deck:minor");

        for (TarotCardDTO card : cards) {
            String cardCode = card.getCardCode();

            // 1. DTO를 JSON String으로 변환해서 String-Value로 저장
            try {
                String cardJson = objectMapper.writeValueAsString(card);
                redisTemplate.opsForValue().set("tarot:card:" + cardCode, cardJson);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 2. Set에 카드 코드 추가
            if ("MAJOR".equalsIgnoreCase(card.getCardType())) {
                redisTemplate.opsForSet().add("tarot:deck:major", cardCode);
            } else {
                redisTemplate.opsForSet().add("tarot:deck:minor", cardCode);
            }
        }

        // 쿠폰 관련
        List<CouponDTO> coupons = testMapper.findAll();

        for (CouponDTO coupon : coupons) {

            String key = "coupon:stock:" + coupon.getCouponCode();

            redisTemplate.opsForValue().set(
                    key,
                    String.valueOf(coupon.getCouponStock())
            );
        }
    }
}
