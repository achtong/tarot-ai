package com.tarot.demo.config;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tarot.demo.DTO.CouponIssueMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponKafkaProducer {
    private final KafkaTemplate<String, CouponIssueMessage> kafkaTemplate;

    private static final String TOPIC = "coupon-issue";

    public void send(CouponIssueMessage message) {
        kafkaTemplate.send(
                TOPIC,
                message.getCouponCode(),
                message
        ).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka 발행 실패: {}", message, ex);
            } else {
                log.debug("Kafka 발행 성공: offset={}",
                    result.getRecordMetadata().offset());
            }
        });
    }
}
