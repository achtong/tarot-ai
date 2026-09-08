import http from "k6/http";
import { check } from "k6";
import { Counter } from "k6/metrics";

const accepted = new Counter("coupon_accepted");
const conflict = new Counter("coupon_conflict");
const errors = new Counter("coupon_errors");
const baseUrl = (__ENV.BASE_URL || "http://localhost:8080").replace(/\/$/, "");
const couponCode = __ENV.COUPON_CODE || "C001";
const userPrefix = __ENV.USER_PREFIX || "user";
const vus = Number(__ENV.VUS || 10000);
const iterations = Number(__ENV.ITERATIONS || 1);

if (!Number.isSafeInteger(vus) || vus < 1 ||
    !Number.isSafeInteger(iterations) || iterations < 1) {
  throw new Error("VUS와 ITERATIONS는 양의 정수여야 합니다.");
}

// coupon_issue.user_id는 VARCHAR(10)이므로 요청 전에 최대 ID 길이를 확인합니다.
const maxUserNumber = vus * iterations;
if (!Number.isSafeInteger(maxUserNumber) ||
    Array.from(userPrefix).length + Math.max(6, String(maxUserNumber).length) > 10) {
  throw new Error("생성되는 userId는 10자 이하여야 합니다. USER_PREFIX를 줄이세요 (예: p003).");
}
const params = {
  headers: { "Content-Type": "application/json" },
};

// 실행 예: k6 run -e COUPON_CODE=C001 -e VUS=100 coupon-test.js
// 비교 실행: k6 run -e VUS=100 -e ITERATIONS=10 coupon-test.js
// 총 요청 = VUS * ITERATIONS. 비교 실행은 동시 100 VU로 서로 다른 사용자 1,000명을 요청합니다.
// 재실행 시 USER_PREFIX를 바꾸면 이전 실행 사용자와의 중복을 피할 수 있습니다.
// 202는 비동기 발급 요청 접수이며, DB 저장 완료는 별도로 확인해야 합니다.

http.setResponseCallback(
  http.expectedStatuses(202, 409)
);

export const options = {
  scenarios: {
    coupon_spike: {
      executor: "per-vu-iterations",
      vus,
      iterations,
      maxDuration: __ENV.MAX_DURATION || "30s",
    },
  },
  thresholds: {
    http_req_failed: ["rate==0"],
    checks: ["rate==1"],
  },
};

export default function () {
  // 반복 요청도 서로 다른 사용자로 처리합니다 (이 스크립트의 단일 로컬 실행 기준).
  const userNumber = __ITER * vus + __VU;
  const userId = `${userPrefix}${String(userNumber).padStart(6, "0")}`;

  const payload = JSON.stringify({
    userId: userId
  });
  const res = http.post(
    `${baseUrl}/api/coupons/${encodeURIComponent(couponCode)}/issue`,
    payload,
    params
  );

  if (res.status === 202) {
    accepted.add(1);
  } else if (res.status === 409) {
    // 현재 API는 품절, 중복 요청, 재고 키 없음 모두 409를 반환합니다.
    conflict.add(1);
  } else {
    errors.add(1, {
      status: String(res.status),
      error_code: String(res.error_code || 0),
    });

    // status=0인 연결 실패도 원인을 확인할 수 있도록 k6 전송 오류를 기록합니다.
    console.error(JSON.stringify({
      userId,
      status: res.status,
      errorCode: res.error_code,
      error: res.error,
      timings: res.timings,
      body: res.body,
    }));
  }

  check(res, {
    "쿠폰 요청 정상 처리": (r) =>
      r.status === 202 || r.status === 409,
  });
}
