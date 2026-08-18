package com.tarot.demo.config;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.mapper.TestMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponRedisInitializer {
    private final TestMapper testMapper;
    private final StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {

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
