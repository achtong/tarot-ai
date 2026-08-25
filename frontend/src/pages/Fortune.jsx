import { useNavigate } from "react-router-dom";
import "../styles/FortuneSelect.css";

function Fortune() {
  const navigate = useNavigate();

  const fortunes = [
    {
      value: "CAREER",
      icon: "💼",
      title: "직장운",
      description: "직장, 이직, 업무와 관련된 고민",
    },
    {
      value: "WEALTH",
      icon: "💰",
      title: "재물운",
      description: "돈, 투자, 재산과 관련된 고민",
    },
    {
      value: "LOVE",
      icon: "❤️",
      title: "연애운",
      description: "연애, 관계, 인연과 관련된 고민",
    },
  ];

  const handleSelect = (fortune) => {
    navigate(`/fortune/concern?category=${fortune.value}`);
  };

  return (
    <main className="fortune-select-page">
      <section className="fortune-select-container">
        <div className="fortune-header">
          <span className="fortune-label">TAROT READING</span>
          <h1>
            어떤 운세를
            <br />
            확인해볼까요?
          </h1>
          <p>고민하고 있는 분야를 선택해주세요.</p>
        </div>

        <div className="fortune-card-list">
          {fortunes.map((fortune) => (
            <button
              key={fortune.value}
              className="fortune-card"
              onClick={() => handleSelect(fortune)}
            >
              <span className="fortune-card-icon">{fortune.icon}</span>

              <span className="fortune-card-text">
                <strong>{fortune.title}</strong>
                <span>{fortune.description}</span>
              </span>

              <span className="fortune-card-arrow">→</span>
            </button>
          ))}
        </div>
      </section>
    </main>
  );
}

export default Fortune;
