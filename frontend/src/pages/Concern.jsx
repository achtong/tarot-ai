import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { useState } from "react";
import "../styles/Concern.css";

function Concern() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const category = searchParams.get("category");

  const [concern, setConcern] = useState("");

  const handleNext = () => {
    if (!concern.trim()) {
      alert("고민을 입력해주세요.");
      return;
    }

    // 고민 내용을 sessionStorage에 저장
    sessionStorage.setItem("concern", concern);

    // category는 URL로 전달
    navigate(`/cards?category=${category}`);
  };

  const getCategoryTitle = () => {
    switch (category) {
      case "CAREER":
        return "💼 직장운";
      case "WEALTH":
        return "💰 재물운";
      case "LOVE":
        return "❤️ 연애운";
      default:
        return "오늘의 타로";
    }
  };

  return (
    <main className="concern-page">
      <section className="concern-container">
        <div className="concern-progress">
          <span className="active">01</span>
          <span>/</span>
          <span>03</span>
        </div>

        <div className="concern-header">
          <span className="selected-category">{getCategoryTitle()}</span>

          <h1>어떤 고민이 있으신가요?</h1>

          <p>현재 고민하고 있는 내용을 편하게 적어주세요.</p>
        </div>

        <div className="concern-input-area">
          <textarea
            value={concern}
            onChange={(e) => setConcern(e.target.value)}
            maxLength={500}
          />

          <div className="character-count">{concern.length} / 500</div>
        </div>

        <button className="next-button" onClick={handleNext}>
          카드 선택하기
          <span>→</span>
        </button>

        <button className="back-button" onClick={() => navigate("/")}>
          ← 운세 다시 선택하기
        </button>
      </section>
    </main>
  );
}

export default Concern;
