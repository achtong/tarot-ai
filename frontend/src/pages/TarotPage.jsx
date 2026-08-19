import React, { useState } from "react";
import TarotGuide from "../components/TarotGuide";
import TarotSlots from "../components/TarotSlots";
import TarotDeck from "../components/TarotDeck";
import "../styles/tarot.css";

export default function TarotPage() {
  const [step, setStep] = useState(1);
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

  // AI 결과 요청 & 카드 Flip 애니메이션 실행
  const handleRevealResults = async () => {
    setStep(5);

    // Mock API 데이터 (추후 Spring Boot API 연동)
    const mockResponse = {
      main: { type: "MAJOR", name: "The Lovers" },
      sub1: { type: "MINOR", name: "2 of Cups" },
      sub2: { type: "MINOR", name: "8 of Swords" },
    };
    setCardsData(mockResponse);

    // 순차적 뒤집기
    setTimeout(() => setFlipped((prev) => ({ ...prev, main: true })), 300);
    setTimeout(() => setFlipped((prev) => ({ ...prev, sub1: true })), 900);
    setTimeout(() => setFlipped((prev) => ({ ...prev, sub2: true })), 1500);
  };

  return (
    <div className="tarot-container">
      <h1>🔮 AI Tarot Reading</h1>

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
    </div>
  );
}
