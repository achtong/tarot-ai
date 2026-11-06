# Coupon System

## Architecture

쿠폰 발급은 다음 구조를 사용한다.

React
→ Spring Boot
→ Redis
→ Kafka
→ MySQL

## Existing API

쿠폰 발급 API:

POST /api/coupons/{couponCode}/issue

- 신규 요청: `202 Accepted` (Kafka 비동기 발급 요청 접수)
- DB 발급 내역 또는 Redis 중복 확인: `409 Conflict`, `COUPON_ALREADY_ISSUED`, "이미 발급받은 쿠폰입니다."
- 재고 소진: `409 Conflict`, `COUPON_SOLD_OUT`, "쿠폰 재고가 모두 소진되었습니다."

DB 중복은 Redis 재고 차감 전에 기존 발급 내역 조회로 확인한다.

## Redis

Redis는 쿠폰 재고의 선착순 처리를 담당한다.

## Kafka

쿠폰 발급 요청은 Kafka를 통해 비동기 처리한다.

## Important

기존 쿠폰 발급 로직은 변경하지 않는 것을 원칙으로 한다.
