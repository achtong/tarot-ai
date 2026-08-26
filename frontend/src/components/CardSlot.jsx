export default function CardSlot({
  type,
  label,
  isActive,
  isMounted,
  isFlipped,
  cardData,
}) {
  return (
    <div
      /* 💡 isFlipped가 false(뒷면)일 때 is-back 클래스 추가 */
      className={`slot ${type} ${!isFlipped ? "is-back" : "is-front"} ${
        isActive ? (type === "main" ? "active-main" : "active-sub") : ""
      }`}
    >
      <span className="slot-label">{label}</span>

      {isMounted && (
        <div className="card-3d">
          <div className={`card-inner ${isFlipped ? "flipped" : ""}`}>
            {/* 카드 뒷면 */}
            <div
              className={`card-face card-back ${type === "main" ? "main-back" : "sub-back"}`}
            >
              🔮
            </div>
            {/* 카드 앞면 */}
            <div className="card-face card-front">
              <div className="card-image-wrapper">
                {cardData?.imageUrl && (
                  <img
                    src={cardData.imageUrl}
                    alt={cardData.name}
                    className="tarot-card-image"
                  />
                )}
              </div>
              {cardData?.name && (
                <div className="card-name-area">{cardData?.name}</div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
