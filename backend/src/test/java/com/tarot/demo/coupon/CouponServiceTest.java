package com.tarot.demo.coupon;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;

import com.tarot.demo.DTO.CouponIssueDTO;
import com.tarot.demo.coupon.service.CouponService;
import com.tarot.demo.exception.CustomException;
@SpringBootTest
class CouponServiceTest {
    @Autowired
    private CouponService couponService;

    @Test
    @DisplayName("1,000명이 동시에 요청 시 선착순 100명만 성공하고 900명은 실패해야 한다/스레드풀 : 32")
    void concurrencyTest_1000Users_100Stock() throws InterruptedException {
        // 1. 테스트 조건 설정
        int totalRequests = 1000; // 총 동시 요청 인원 (1,000명)
        int expectedSuccess = 100; // 예상 성공 수량 (재고 100개)
        int expectedFail = 900;    // 예상 실패 수량 (900명)
        // 시작 시간
        long startTime = System.currentTimeMillis();
        // 동시에 요청을 퍼부을 스레드 풀 (32~64개로 병렬 처리)
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        String testCouponCode = "C001"; // 테스트할 쿠폰 코드
        
        // 1,000개의 스레드가 모두 종료될 때까지 대기하기 위한 래치
        CountDownLatch latch = new CountDownLatch(totalRequests);

        // 동시성 환경에서도 안전하게 카운트할 수 있는 AtomicInteger
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // 2. 1,000명의 동시 요청 실행
        for (int i = 0; i < totalRequests; i++) {
            int userId = i + 1;
            CouponIssueDTO dto = new CouponIssueDTO();
            dto.setUserId(String.valueOf(userId));
            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(dto, testCouponCode);

                    // 예외가 발생하지 않았다면 성공
                    successCount.incrementAndGet();

                } catch (CustomException e) {
                    // 쿠폰 재고 부족 등 예상된 실패
                    failCount.incrementAndGet();

                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 1,000개 스레드의 처리가 끝날 때까지 대기
        latch.await();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // 3. 테스트 결과 출력
        System.out.println("=========================================");
        System.out.println("TotalRequests: " + totalRequests);
        System.out.println("TotalTime " + totalTime + " ms");
        System.out.println("Success: " + successCount.get());
        System.out.println("Fail: " + failCount.get());
        System.out.println("=========================================");

        // 4. 검증 (Assertion)
        assertThat(successCount.get()).isEqualTo(expectedSuccess); // 성공은 정확히 100개여야 함
        assertThat(failCount.get()).isEqualTo(expectedFail);       // 실패는 정확히 900개여야 함
    }

    @Test
    @DisplayName("동일한 요청 메시지가 중복 전달되어도 DB에는 1건만 저장되어 멱등성이 보장")
    void testIdempotencyWithDuplicateMessage() throws Exception {
        // 동일한 요청 데이터 준비
        String targetUserId = "user_test_999";
        String couponCode = "C001";

        CouponIssueDTO duplicateDto = new CouponIssueDTO();
        duplicateDto.setCouponCode(couponCode);
        duplicateDto.setUserId(targetUserId);

        // 2번 호출하여 멱등성 보장 여부 파악
        // 1번째 호출 (정상 저장되어야 함)
        couponService.issueCoupon(duplicateDto, couponCode);

        // 2번째 호출
        couponService.issueCoupon(duplicateDto, couponCode);
        
        // DB에 해당 유저의 발급 내역이 정확히 '1건'만 존재하는지 검증
         await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(500, TimeUnit.MILLISECONDS)
        .untilAsserted(() -> {
            int savedCount = couponService.countCoupon(duplicateDto);
            assertThat(savedCount).isEqualTo(1);
        });
    }
}
