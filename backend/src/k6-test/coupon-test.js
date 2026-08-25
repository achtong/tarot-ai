import http from "k6/http";
import { check } from "k6";

export const options = {
  scenarios: {
    // 1000명의 가상 유저가 거의 동시에(1초 내) 도달하는 Spike 테스트
    coupon_spike: {
      executor: "per-vu-iterations",
      vus: 1000, // 동시 접속 가상 유저 1,000명
      iterations: 1, // 각 유저당 1번씩 쿠폰 요청 클릭
      maxDuration: "10s",
    },
  },
};

export default function () {
  // PathVariable로 들어갈 쿠폰 코드 설정
  const couponCode = "C001";
  // 컨트롤러 매핑 주소에 맞춘 최종 URL
  const url = `http://localhost:8080/api/coupons/${couponCode}/issue`;

  // 3. @RequestBody로 들어갈 JSON 데이터 (유저 ID 등)
  const payload = JSON.stringify({
    userId: __VU, // 1~1000번 가상 유저 ID
    couponCode: couponCode,
  });

  // Header 및 POST 요청 발송
  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const res = http.post(url, payload, params);

  // 5. 응답 결과 체크
  check(res, {
    "발급 성공 (200 OK)": (r) => r.status === 200,
    "선착순 마감 (409 Conflict)": (r) => r.status === 409,
  });
}
