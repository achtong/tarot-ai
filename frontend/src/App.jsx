import { useEffect, useState } from "react";
import axios from "axios";

function App() {
  const [coupons, setCoupons] = useState([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/test")
      .then((response) => {
        console.log(response.data);
        setCoupons(response.data);
      })
      .catch((error) => {
        console.error("API 호출 실패:", error);
      });
  }, []);

  const handleCouponIssue = async () => {
    const start = performance.now();

    const requests = [];

    for (let i = 1; i <= 1000; i++) {
      requests.push(
        axios.post("http://localhost:8080/api/coupons/C001/issue", {
          userId: `user${String(i).padStart(4, "0")}`,
        }),
      );
    }

    const results = await Promise.allSettled(requests);

    const end = performance.now();

    const success = results.filter(
      (result) => result.status === "fulfilled",
    ).length;

    const failed = results.filter(
      (result) => result.status === "rejected",
    ).length;

    console.log("총 요청:", results.length);
    console.log("성공:", success);
    console.log("실패:", failed);
    console.log("처리 시간:", `${(end - start).toFixed(2)} ms`);
  };

  return (
    <div>
      <button onClick={handleCouponIssue}>쿠폰 발급 테스트</button>
      <h1>쿠폰 목록</h1>

      {coupons.map((coupon) => (
        <div key={coupon.couponCode}>
          <p>쿠폰 코드: {coupon.couponCode}</p>
          <p>쿠폰 이름: {coupon.couponName}</p>
          <p>쿠폰 재고: {coupon.couponStock}</p>
        </div>
      ))}
    </div>
  );
}

export default App;
