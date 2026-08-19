import React from "react";

export default function TarotDeck({ step, onPickCard }) {
  if (step > 3) return null; // 3장 다 뽑으면 덱 숨김

  const cardCount = 9;
  const isMajor = step === 1;

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
}
