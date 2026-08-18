package com.tarot.demo.config;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tarot.demo.DTO.CouponIssueMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponKafkaProducer {
     private final KafkaTemplate<String, CouponIssueMessage> kafkaTemplate;

    private static final String TOPIC = "coupon-issue";

    public void send(CouponIssueMessage message) {

        kafkaTemplate.send(
                TOPIC,
                message.getCouponCode(),
                message
        );
    }
}
