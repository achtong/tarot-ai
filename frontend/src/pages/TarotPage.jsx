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
  const [readingId, setReadingId] = useState(null);
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
    try {
      const response = await axios.get("http://localhost:8080/api/tarot/3card");
      const { readingId, cards } = response.data;

      // 나중에 AI 분석 요청할 때 사용
      setReadingId(readingId);

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
      setReadingId(response.data.readingId);
      setStep(5);
      // 순차적 뒤집기
      setTimeout(() => setFlipped((prev) => ({ ...prev, main: true })), 300);
      setTimeout(() => setFlipped((prev) => ({ ...prev, sub1: true })), 900);
      setTimeout(() => setFlipped((prev) => ({ ...prev, sub2: true })), 1500);

      const aiResponse = await axios.post(
        "http://localhost:8080/api/tarot/analyze",
        {
          readingId: readingId,
          category: category,
          concern: concern,
        },
      );
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
    </div>
  );
}
