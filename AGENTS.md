# AGENTS.md

## Project

이 프로젝트는 React + Spring Boot 기반의 AI 타로 서비스이다.

## Architecture

Frontend:

- React
- Vite
- Axios

Backend:

- Spring Boot
- Java 17
- MySQL
- Redis
- Kafka

AI:

- 타로 카드 분석
- AI 분석 결과 제공

## Important Existing Features

이 프로젝트에는 기존에 구현된 쿠폰 발급 시스템이 존재한다.

쿠폰 발급 시스템은 Redis와 Kafka를 이용한
선착순 쿠폰 발급 구조로 구현되어 있다.

기존 쿠폰 발급 로직은 가능한 한 재사용한다.

기존 쿠폰 발급 로직을 새로 작성하지 않는다.

## Tarot Feature

사용자가 타로 카드 3장을 선택하고
고민을 입력하면 AI가 타로 분석 결과를 제공한다.

분석 결과 화면에는 쿠폰 발급 버튼을 추가한다.

## Coupon Feature

타로 분석 완료 후 사용자가 쿠폰 발급 버튼을 누르면
기존 쿠폰 발급 API를 호출한다.

새로운 쿠폰 발급 시스템을 만들지 않는다.

기존 API가 있다면 해당 API를 재사용한다.

## Development Rules

1. 작업 전에 기존 코드를 반드시 확인한다.
2. 기존 쿠폰 Controller / Service / Redis / Kafka 로직을 확인한다.
3. 기존 API를 재사용할 수 있다면 새로운 API를 만들지 않는다.
4. 필요한 경우에만 기존 코드를 최소한으로 수정한다.
5. 프론트엔드에서는 기존 Axios API 구조를 따른다.
6. 작업 완료 후 관련 테스트를 실행한다.
7. 기존 쿠폰 발급 기능이 정상적으로 동작하는지 확인한다.
8. 불필요한 리팩터링을 하지 않는다.
