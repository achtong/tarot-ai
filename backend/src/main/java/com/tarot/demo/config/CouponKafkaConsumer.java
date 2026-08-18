package com.tarot.demo.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tarot.demo.DTO.CouponIssueDTO;
import com.tarot.demo.DTO.CouponIssueMessage;
import com.tarot.demo.mapper.TestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponKafkaConsumer {
     private final TestMapper testMapper;

    @KafkaListener(
            topics = "coupon-issue",
            groupId = "coupon-issue-group"
    )

    @Transactional
    public void consume(CouponIssueMessage message) {
        
        int updated = testMapper.updateCouponStock(
            message.getCouponCode()
        );

        if (updated == 0) {
            throw new IllegalStateException("DB 재고 차감 실패");
        }

        log.info("Kafka 메시지 수신: {}", message);

        CouponIssueDTO DTO = new CouponIssueDTO();

        DTO.setUserId(message.getUserId());
        DTO.setCouponCode(message.getCouponCode());

        testMapper.coupon(DTO);
    }
}
