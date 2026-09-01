import { useEffect, useState } from "react";
import axios from "axios";

function Test() {
  const [coupons, setCoupons] = useState([]);
  const [useCouponCode, setUseCouponCode] = useState("");
  const [useUserId, setUseUserId] = useState("");
  const [deleteCouponCode, setDeleteCouponCode] = useState("");
  const [deleteUserId, setDeleteUserId] = useState("");
  const [message, setMessage] = useState("");

  const getCoupons = () => {
    axios
      .get("http://localhost:8080/api/test")
      .then((response) => {
        console.log(response.data);
        setCoupons(response.data);
      })
      .catch((error) => {
        console.error("API 호출 실패:", error);
      });
  };

  useEffect(() => {
    getCoupons();
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

    setTimeout(() => {
      getCoupons();
    }, 2000);
  };

  const handleCouponUse = async () => {
    const couponCode = useCouponCode.trim();
    const userId = useUserId.trim();

    if (!couponCode || !userId) {
      setMessage("사용할 쿠폰 코드와 사용자 ID를 입력해주세요.");
      return;
    }

    try {
      await axios.patch(
        `http://localhost:8080/api/coupons/${encodeURIComponent(couponCode)}/issues/${encodeURIComponent(userId)}`,
      );
      setMessage(`${userId} 사용자의 ${couponCode} 쿠폰을 사용했습니다.`);
      setUseCouponCode("");
      setUseUserId("");
    } catch (error) {
      setMessage(
        error.response?.data?.message || "쿠폰 사용 중 오류가 발생했습니다.",
      );
    }
  };

  const handleCouponDelete = async () => {
    const couponCode = deleteCouponCode.trim();
    const userId = deleteUserId.trim();

    if (!couponCode || !userId) {
      setMessage("삭제할 쿠폰 코드와 사용자 ID를 입력해주세요.");
      return;
    }

    try {
      await axios.delete(
        `http://localhost:8080/api/coupons/${encodeURIComponent(couponCode)}/issues/${encodeURIComponent(userId)}`,
      );
      setMessage(`${userId} 사용자의 ${couponCode} 발급 내역을 삭제했습니다.`);
      setDeleteCouponCode("");
      setDeleteUserId("");
    } catch (error) {
      setMessage(
        error.response?.data?.message || "쿠폰 삭제 중 오류가 발생했습니다.",
      );
    }
  };

  return (
    <div>
      <button onClick={handleCouponIssue}>쿠폰 발급 테스트</button>
      <div>
        <h2>쿠폰 사용</h2>
        <label htmlFor="use-coupon-code">쿠폰 코드</label>
        <input
          id="use-coupon-code"
          type="text"
          value={useCouponCode}
          onChange={(event) => setUseCouponCode(event.target.value)}
          placeholder="예: C001"
        />
        <label htmlFor="use-user-id">사용자 ID</label>
        <input
          id="use-user-id"
          type="text"
          value={useUserId}
          onChange={(event) => setUseUserId(event.target.value)}
          placeholder="사용자 ID 입력"
        />
        <button type="button" onClick={handleCouponUse}>
          사용
        </button>
      </div>
      <div>
        <h2>발급 쿠폰 삭제</h2>
        <label htmlFor="delete-coupon-code">쿠폰 코드</label>
        <input
          id="delete-coupon-code"
          type="text"
          value={deleteCouponCode}
          onChange={(event) => setDeleteCouponCode(event.target.value)}
          placeholder="예: C001"
        />
        <label htmlFor="delete-user-id">사용자 ID</label>
        <input
          id="delete-user-id"
          type="text"
          value={deleteUserId}
          onChange={(event) => setDeleteUserId(event.target.value)}
          placeholder="사용자 ID 입력"
        />
        <button type="button" onClick={handleCouponDelete}>
          삭제
        </button>
      </div>
      {message && <p role="status">{message}</p>}
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

export default Test;
