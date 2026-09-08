package com.tarot.demo.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.tarot.demo.DTO.CouponIssueDTO;
import com.tarot.demo.DTO.CouponIssueMessage;
import com.tarot.demo.coupon.mapper.CouponMapper;

@Component
public class CouponKafkaConsumer {
    private final CouponMapper couponMapper;
    private final TransactionTemplate transaction;

    public CouponKafkaConsumer(CouponMapper couponMapper, PlatformTransactionManager transactionManager) {
        this.couponMapper = couponMapper;
        this.transaction = new TransactionTemplate(transactionManager);
    }

    @KafkaListener(topics = "coupon-issue", groupId = "coupon-issue-group", concurrency = "3")
    public void consume(CouponIssueMessage message) {
        // 예외를 삼키지 않고 롤백 후 Kafka 재시도로 전달합니다.
        transaction.executeWithoutResult(status -> {
            // 외래키 검사 전에 부모 행을 잠가 공유 잠금의 승격 충돌을 방지합니다.
            if (couponMapper.lockCoupon(message.getCouponCode()) == null) {
                throw new IllegalStateException("쿠폰을 찾을 수 없습니다: " + message.getCouponCode());
            }
            CouponIssueDTO dto = new CouponIssueDTO();
            dto.setUserId(message.getUserId());
            dto.setCouponCode(message.getCouponCode());
            // 쿠폰 잠금을 얻은 뒤 중복을 확인하므로 재고 0에서 재전달되어도 안전합니다.
            if (couponMapper.countCoupon(dto) > 0) {
                return;
            }
            if (couponMapper.updateCouponStock(message.getCouponCode()) != 1) {
                throw new IllegalStateException("DB 재고 차감 실패: " + message.getCouponCode());
            }
            couponMapper.coupon(dto);
        });
    }
}
