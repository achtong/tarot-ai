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

POST /api/coupons/{couponId}/issue

## Redis

Redis는 쿠폰 재고의 선착순 처리를 담당한다.

## Kafka

쿠폰 발급 요청은 Kafka를 통해 비동기 처리한다.

## Important

기존 쿠폰 발급 로직은 변경하지 않는 것을 원칙으로 한다.
