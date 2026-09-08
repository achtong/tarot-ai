package com.tarot.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class CouponKafkaErrorConfig {
    @Bean
    public DefaultErrorHandler couponKafkaErrorHandler() {
        // 영구 오류는 해결할 때까지 진행을 막으며 실패 메시지를 버리지 않습니다.
        return new DefaultErrorHandler((record, exception) -> {
            throw new IllegalStateException("쿠폰 메시지를 버리지 않고 유지합니다", exception);
        }, new FixedBackOff(1000L, FixedBackOff.UNLIMITED_ATTEMPTS));
    }
}
