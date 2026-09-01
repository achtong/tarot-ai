import { useSearchParams } from "react-router-dom";
import { useState } from "react";
import TarotGuide from "../components/TarotGuide";
import TarotSlots from "../components/TarotSlots";
import TarotDeck from "../components/TarotDeck";
import "../styles/tarot.css";
import axios from "axios";

export default function TarotPage() {
  const [searchParams] = useSearchParams();
  const category = searchParams.get("category");
  const concern = sessionStorage.getItem("concern");
  const [step, setStep] = useState(1);

  // AI 분석 결과
  const [analysisResult, setAnalysisResult] = useState("");
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisError, setAnalysisError] = useState("");

  // 쿠폰 발급
  const [userId, setUserId] = useState("");
  const [isIssuingCoupon, setIsIssuingCoupon] = useState(false);
  const [couponResult, setCouponResult] = useState({ type: "", message: "" });

  const [mounted, setMounted] = useState({
    main: false,
    sub1: false,
    sub2: false,
  });
  const [flipped, setFlipped] = useState({
    main: false,
    sub1: false,
    sub2: false,
  });
  const [cardsData, setCardsData] = useState({
    main: null,
    sub1: null,
    sub2: null,
  });

  // 카드 뽑기 클릭 이벤트
  const handlePickCard = () => {
    if (step === 1) {
      setMounted((prev) => ({ ...prev, main: true }));
      setStep(2);
    } else if (step === 2) {
      setMounted((prev) => ({ ...prev, sub1: true }));
      setStep(3);
    } else if (step === 3) {
      setMounted((prev) => ({ ...prev, sub2: true }));
      setStep(4);
    }
  };

  const handleAnalyze = async (currentReadingId) => {
    setIsAnalyzing(true);
    setAnalysisError("");
    try {
      const response = await axios.post(
        "http://localhost:8080/api/tarot/analyze",
        { readingId: currentReadingId, category: category, concern: concern },
      );
      console.log("AI 분석 결과:", response.data);
      setAnalysisResult(response.data);
    } catch (error) {
      console.error("AI 분석 실패:", error);
      if (error.response?.status === 429) {
        setAnalysisError(
          "현재 AI 분석 요청이 많습니다. 잠시 후 다시 시도해주세요.",
        );
      } else {
        setAnalysisError("AI 분석 중 오류가 발생했습니다.");
      }
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleCouponIssue = async () => {
    const trimmedUserId = userId.trim();

    if (!trimmedUserId) {
      setCouponResult({ type: "error", message: "ID를 입력해주세요." });
      return;
    }

    setIsIssuingCoupon(true);
    setCouponResult({ type: "", message: "" });

    try {
      await axios.post(
        "http://localhost:8080/api/coupons/C001/issue",
        { userId: trimmedUserId },
      );

      setCouponResult({
        type: "success",
        message: "쿠폰 발급 요청이 접수되었습니다.",
      });
    } catch (error) {
      const message =
        error.response?.data?.message ||
        (error.response?.status === 409
          ? "쿠폰이 모두 소진되었거나 이미 발급받았습니다."
          : "쿠폰 발급 중 오류가 발생했습니다.");

      setCouponResult({ type: "error", message });
    } finally {
      setIsIssuingCoupon(false);
    }
  };

  // AI 결과 요청 & 카드 Flip 애니메이션 실행
  const handleRevealResults = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/tarot/3card");
      const { readingId: newReadingId, cards } = response.data;

      const responseData = {
        main: {
          type: cards[0].cardType,
          name: cards[0].nameKr,
          imageUrl: cards[0].imageUrl,
        },
        sub1: {
          type: cards[1].cardType,
          name: cards[1].nameKr,
          imageUrl: cards[1].imageUrl,
        },
        sub2: {
          type: cards[2].cardType,
          name: cards[2].nameKr,
          imageUrl: cards[2].imageUrl,
        },
      };

      setCardsData(responseData);
      setStep(5);
      // 순차적 뒤집기
      setTimeout(() => setFlipped((prev) => ({ ...prev, main: true })), 300);
      setTimeout(() => setFlipped((prev) => ({ ...prev, sub1: true })), 900);
      setTimeout(() => setFlipped((prev) => ({ ...prev, sub2: true })), 1500);

      setTimeout(() => {
        handleAnalyze(newReadingId);
      }, 1800);
    } catch (error) {
      console.error("카드 조회 실패:", error);
    }
  };

  return (
    <div className="tarot-container">
      <h1>AI 타로 분석</h1>
      {/* 1. 가이드 문구 */}
      <TarotGuide step={step} />
      {/* 2. 카드 슬롯 컨테이너 */}
      <TarotSlots
        step={step}
        mounted={mounted}
        flipped={flipped}
        cardsData={cardsData}
      />
      {/* 3. 하단 카드 덱 */}
      <TarotDeck step={step} onPickCard={handlePickCard} />
      {/* 4. 결과 보기 버튼 */}
      {step === 4 && (
        <button className="result-btn" onClick={handleRevealResults}>
          AI 해석 보기
        </button>
      )}
      {step === 5 && isAnalyzing && (
        <div className="analysis-loading">
          {" "}
          <div className="loading-spinner"></div>{" "}
          <p> 🔮 AI 타로술사가 카드를 해석하고 있습니다... </p>{" "}
          <span> 잠시만 기다려주세요. </span>{" "}
        </div>
      )}{" "}
      {/* 6. AI 분석 오류 */}{" "}
      {analysisError && (
        <div className="analysis-error">
          {" "}
          <p>{analysisError}</p>{" "}
        </div>
      )}{" "}
      {/* 7. AI 분석 결과 */}{" "}
      {analysisResult && !isAnalyzing && (
        <div className="analysis-result">
          {" "}
          <h2>🔮 타로 해석</h2>{" "}
          <div className="analysis-content"> {analysisResult} </div>{" "}
          <div className="coupon-issue-area">
            <h3>선착순 쿠폰 받기</h3>
            <p>C001 쿠폰을 받을 ID를 입력해주세요.</p>
            <div className="coupon-issue-form">
              <input
                type="text"
                value={userId}
                onChange={(event) => {
                  setUserId(event.target.value);
                  setCouponResult({ type: "", message: "" });
                }}
                placeholder="ID 입력"
                disabled={isIssuingCoupon || couponResult.type === "success"}
                aria-label="쿠폰을 받을 ID"
              />
              <button
                type="button"
                className="coupon-issue-btn"
                onClick={handleCouponIssue}
                disabled={isIssuingCoupon || couponResult.type === "success"}
              >
                {isIssuingCoupon ? "발급 중..." : "쿠폰 받기"}
              </button>
            </div>
            {couponResult.message && (
              <p
                className={`coupon-result ${couponResult.type}`}
                role="status"
              >
                {couponResult.message}
              </p>
            )}
          </div>
        </div>
      )}{" "}
    </div>
  );
}
