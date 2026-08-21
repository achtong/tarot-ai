package com.tarot.demo.config;

import java.util.concurrent.atomic.AtomicInteger;

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
     private final AtomicInteger count = new AtomicInteger(0);

     private long startTime;

    @KafkaListener(
            topics = "coupon-issue",
            groupId = "coupon-issue-group"
    )

    @Transactional
    public void consume(CouponIssueMessage message) {
        
        // 첫 번째 메시지가 들어온 시간
        if (count.get() == 0) {
            startTime = System.nanoTime();
        }

        int updated = testMapper.updateCouponStock(
            message.getCouponCode()
        );

        if (updated == 0) {
            throw new IllegalStateException("DB 재고 차감 실패");
        }

        CouponIssueDTO DTO = new CouponIssueDTO();

        DTO.setUserId(message.getUserId());
        DTO.setCouponCode(message.getCouponCode());

        testMapper.coupon(DTO);
        int current = count.incrementAndGet();

        // 100개 처리 완료
        if (current == 100) {
            long endTime = System.nanoTime();

            double elapsedMs =
                (endTime - startTime) / 1_000_000.0;

            log.info(
                "===== Kafka Consumer 100개 처리 완료 ====="
            );
            log.info(
                "처리시간: {} ms",
                elapsedMs
            );
        }
    }
}
