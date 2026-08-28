package com.tarot.demo.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.DTO.CouponIssueDTO;
import com.tarot.demo.DTO.CouponIssueMessage;
import com.tarot.demo.config.CouponKafkaProducer;
import com.tarot.demo.exception.CustomException;
import com.tarot.demo.exception.ErrorCode;
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
    -- KEYS[1]: coupon:stock:{couponCode}      (재고 카운터)
    -- KEYS[2]: coupon:issued:{couponCode}     (발급받은 유저 Set)
    -- ARGV[1]: userId

    -- 이미 발급받은 유저인지 먼저 체크
    if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
        return -2  -- 중복 요청
    end

    local stock = redis.call('GET', KEYS[1])

    if not stock then
        return -1  -- 재고 키 자체가 없음
    end

    if tonumber(stock) <= 0 then
        return 0   -- 재고 소진
    end

    -- 2. 재고 차감 + 유저를 발급 완료 목록에 추가 (원자적으로 같이 처리)
    redis.call('DECR', KEYS[1])
    redis.call('SADD', KEYS[2], ARGV[1])

    return 1  -- 성공
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

    public void issueCoupon(CouponIssueDTO DTO, String couponCode) {

        String stockKey = "coupon:stock:" + couponCode;
        String issuedKey = "coupon:issued:" + couponCode;
        
        RedisScript<Long> script = RedisScript.of(
            STOCK_DECREASE_SCRIPT,
            Long.class
        );

        Long result = redisTemplate.execute(
            script,
            Arrays.asList(stockKey, issuedKey),
            DTO.getUserId()
    );

        if (result == null || result != 1) {
            throw new CustomException(ErrorCode.COUPON_SOLD_OUT);
        }

        CouponIssueMessage message =
            new CouponIssueMessage(
                    DTO.getUserId(),
                    couponCode
            );

        couponKafkaProducer.send(message);
    }

    public int countCoupon (CouponIssueDTO dto) {
        return testMapper.countCoupon(dto);
    }
    

    

}
