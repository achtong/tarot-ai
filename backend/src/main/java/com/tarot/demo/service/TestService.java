package com.tarot.demo.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.DTO.CouponIssueDTO;
import com.tarot.demo.DTO.CouponIssueMessage;
import com.tarot.demo.config.CouponKafkaProducer;
import com.tarot.demo.mapper.TestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final StringRedisTemplate redisTemplate;
    private final CouponKafkaProducer couponKafkaProducer;
    private final TestMapper testMapper;
    private static final String STOCK_DECREASE_SCRIPT = """
    local stock = redis.call('GET', KEYS[1])

    if not stock then
        return -1
    end

    if tonumber(stock) <= 0 then
        return 0
    end

    redis.call('DECR', KEYS[1])
    return 1
    """;

    public List<CouponDTO> findAll() {
        return testMapper.findAll();
    }

    @Transactional
    public boolean coupon(CouponIssueDTO DTO, String CouponCode){
        long start = System.currentTimeMillis();
        int update = testMapper.updateCouponStock(CouponCode);

        if(update == 0){
            long end = System.currentTimeMillis();
            log.info("품절 처리 시간: {} ms",end - start);
            return false;
        } 
        DTO.setCouponCode(CouponCode); 
        testMapper.coupon(DTO); 
        long end = System.currentTimeMillis();
        log.info("쿠폰 발급 처리 시간: {} ms", end - start);
        return true;
    }

    public boolean issueCoupon(CouponIssueDTO DTO, String couponCode) {

        String key = "coupon:stock:" + couponCode;

        RedisScript<Long> script = RedisScript.of(
            STOCK_DECREASE_SCRIPT,
            Long.class
        );

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key)
        );

        if (result == null || result != 1) {
            return false;
        }

        CouponIssueMessage message =
            new CouponIssueMessage(
                    DTO.getUserId(),
                    couponCode
            );

        couponKafkaProducer.send(message);

        return true;
    }

    

}
