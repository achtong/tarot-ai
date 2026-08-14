# tarot-ai
## 프로젝트 소개
사용자가 입력한 질문이나 상황을 바탕으로 AI 에이전트가 타로 카드를 추천하고, 
해당 카드 조합에 따른 맞춤형 운세 해석 결과를 제공하고, 타로에 참가한 사람들에게 선착순 쿠폰을 발급 및 사용, 삭제 등을 구현한 RESTful API 서버입니다.


## 📌 프로젝트 기획 배경
* **AI 서비스 경험 제공:** 사용자가 고른 3개의 카드를 AI 에이전트가 분석하여 오늘의 운세를 보여줌
* **대용량 트래픽 및 동시성 검증:** 타로 분석 완료 이용자를 대상으로 한 '선착순 쿠폰 이벤트' 상황을 가상 구축하여, 순간 트래픽 스파이크(Traffic Spike) 시의 동시성 제어 및 데이터 정합성 보장 구조 설계

### 1. RESTful API 설계 및 상태 관리 (Coupon Domain)
* **쿠폰 상태 라이프사이클 관리:** 쿠폰 발급(`POST`), 쿠폰 사용 및 삭제(`PUT /coupons/{id}/use`, `DELETE /coupons/{id}/`)에 대한 RESTful API 설계
* **데이터 정합성 및 예외 처리:** 중복 사용 방지, 사용 취소 시 트랜잭션 보장 및 `@RestControllerAdvice` 기반 예외 커스터마이징

### 1. 백엔드 & API Architecture
* RESTful API 설계 및 구현:** API 리소스 규격에 맞춘 End-point 설계 및 전역 예외 처리 적용
* AI 에이전트 연동:** 타로 프롬프트 파이프라인 구성 및 LLM 응답 데이터 구조화(JSON Format)

### 2. 메시징 & 대용량 트래픽 처리 (Kafka & Redis)
* **Kafka 기반 비동기 이벤트 처리:** 선착순 쿠폰 발급 저장 로직을 비동기 이벤트 기반(Event-Driven)으로 분리하여 응답 대기 시간 단축
* **Redis & Kafka 부하 분산(Load Leveling):** 순간 트래픽 스파이크 발생 시 Redis로 앞단 제어 후, Kafka Topic에 요청을 적재하여 DB 쓰기 부하를 안정적으로 제어

### 3. 인프라 & 컨테이너 환경 구축
* **Docker Compose 기반 구성:** Application, Database, **Kafka Broker 환경을 컨테이너화하여 단일 명령어로 전체 개발/운영 환경 실행 구축

---

## 🏗️ Tech Stack
* **Language / Framework:** Java 17, Spring Boot
* **Front** : React
* **Database:** MySQL, Redis
* **Message Broker:** Kafka
* **DevOps / Infra:** Docker, Docker Compose
* **AI:** Claude
