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
      className={`slot ${type} ${isActive ? (type === "main" ? "active-main" : "active-sub") : ""}`}
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
              {cardData?.imageUrl && (
                <img
                  src={cardData.imageUrl}
                  alt={cardData.name}
                  className="tarot-card-image"
                />
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
