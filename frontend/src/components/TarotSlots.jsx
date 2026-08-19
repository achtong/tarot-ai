import React from "react";
import CardSlot from "./CardSlot";

export default function TarotSlots({ step, mounted, flipped, cardsData }) {
  return (
    <div className="slots-container">
      {/* 메인 카드 슬롯 */}
      <CardSlot
        type="main"
        label="MAIN (메이저)"
        isActive={step === 1}
        isMounted={mounted.main}
        isFlipped={flipped.main}
        cardData={cardsData.main}
      />

      {/* 서브 카드 슬롯 2개 */}
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
  );
}
