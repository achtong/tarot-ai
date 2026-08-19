import React, { useState } from "react";
import "../styles/tarot.css";

// 1. 하단 부채꼴 덱 컴포넌트
const TarotDeck = ({ step, onPickCard }) => {
  if (step > 3) return null; // 3장 다 뽑으면 덱 숨김

  const cardCount = 9;
  const isMajor = step === 1; // 1단계만 메이저 덱(금빛)

  return (
    <div className="deck-wrapper">
      {Array.from({ length: cardCount }).map((_, i) => {
        const rotateDeg = (i - (cardCount - 1) / 2) * 6;
        const translateX = (i - (cardCount - 1) / 2) * 18;

        return (
          <div
            key={i}
            className={`deck-card ${isMajor ? "major-deck" : "minor-deck"}`}
            style={{
              transform: `translateX(${translateX}px) rotate(${rotateDeg}deg)`,
            }}
            onClick={onPickCard}
          />
        );
      })}
    </div>
  );
};

// 개별 3D 카드 슬롯 컴포넌트
const CardSlot = ({
  type,
  label,
  isActive,
  isMounted,
  isFlipped,
  cardData,
}) => {
  return (
    <div
      className={`slot ${type} ${isActive ? (type === "main" ? "active-main" : "active-sub") : ""}`}
    >
      <span className="slot-label">{label}</span>

      {isMounted && (
        <div className="card-3d">
          <div className={`card-inner ${isFlipped ? "flipped" : ""}`}>
            {/* 뒷면 */}
            <div
              className={`card-face card-back ${type === "main" ? "main-back" : "sub-back"}`}
            >
              🔮
            </div>
            {/* 앞면 */}
            <div className="card-face card-front">
              <span
                style={{
                  fontSize: "0.65rem",
                  color: "#666",
                  fontWeight: "bold",
                }}
              >
                {cardData?.type}
              </span>
              <span
                style={{
                  fontSize: "0.85rem",
                  fontWeight: "bold",
                  marginTop: "4px",
                }}
              >
                {cardData?.name}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// 메인 타로 페이지 컴포넌트
export default function TarotPage() {
  // 진행 단계: 1(메인), 2(서브1), 3(서브2), 4(뽑기완료), 5(해석중/완료)
  const [step, setStep] = useState(1);

  // 슬롯에 카드가 꽂혔는지 여부
  const [mounted, setMounted] = useState({
    main: false,
    sub1: false,
    sub2: false,
  });

  // 카드가 뒤집혔는지 여부
  const [flipped, setFlipped] = useState({
    main: false,
    sub1: false,
    sub2: false,
  });

  // 백엔드 API에서 받아올 카드 데이터 상태
  const [cardsData, setCardsData] = useState({
    main: null,
    sub1: null,
    sub2: null,
  });

  // 카드를 클릭했을 때 (카드 뽑기 이벤트)
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

  // [AI 해석 보기] 버튼 클릭 시 실행 (Spring Boot API 연동 시점)
  const handleRevealResults = async () => {
    setStep(5);

    // TODO: 백엔드 API 호출 예시 (POST /api/v1/tarot/draw)
    // const res = await fetch('/api/v1/tarot/draw', { method: 'POST', body: ... });
    // const data = await res.json();

    // 임시 Mock 데이터 세팅
    const mockResponse = {
      main: { type: "MAJOR", name: "The Lovers" },
      sub1: { type: "MINOR", name: "2 of Cups" },
      sub2: { type: "MINOR", name: "8 of Swords" },
    };
    setCardsData(mockResponse);

    // 순차적 카드 Flip 애니메이션 연출
    setTimeout(() => setFlipped((prev) => ({ ...prev, main: true })), 300);
    setTimeout(() => setFlipped((prev) => ({ ...prev, sub1: true })), 900);
    setTimeout(() => setFlipped((prev) => ({ ...prev, sub2: true })), 1500);
  };

  // Step별 가이드 텍스트
  const getGuideMessage = () => {
    switch (step) {
      case 1:
        return "고민의 핵심 흐름인 [메인 카드]를 뽑아주세요!";
      case 2:
        return "세부 상황을 나타낼 [첫 번째 서브 카드]를 뽑아주세요!";
      case 3:
        return "해결책과 조언을 담은 [두 번째 서브 카드]를 뽑아주세요!";
      case 4:
        return "모든 카드를 뽑았습니다! 아래 버튼을 눌러 점쾌를 확인해보세요.";
      case 5:
        return "🔮 AI 타로술사가 카드를 해석하는 중입니다...";
      default:
        return "";
    }
  };

  return (
    <div className="tarot-container">
      <h1>🔮 AI Tarot Reading</h1>
      <div className="guide-text">{getGuideMessage()}</div>

      {/* 상단 카드 슬롯 영역 */}
      <div class="slots-container">
        <CardSlot
          type="main"
          label="MAIN (메이저)"
          isActive={step === 1}
          isMounted={mounted.main}
          isFlipped={flipped.main}
          cardData={cardsData.main}
        />
        <div className="sub-slots">
          <CardSlot
            type="sub"
            label="SUB 1"
            isActive={step === 2}
            isMounted={mounted.sub1}
            isFlipped={flipped.sub1}
            cardData={cardsData.sub1}
          />
          <CardSlot
            type="sub"
            label="SUB 2"
            isActive={step === 3}
            isMounted={mounted.sub2}
            isFlipped={flipped.sub2}
            cardData={cardsData.sub2}
          />
        </div>
      </div>

      {/* 하단 부채꼴 카드 덱 */}
      <TarotDeck step={step} onPickCard={handlePickCard} />

      {/* Step 4일 때 결과 보기 버튼 활성화 */}
      {step === 4 && (
        <button className="result-btn" onClick={handleRevealResults}>
          AI 해석 보기
        </button>
      )}
    </div>
  );
}
