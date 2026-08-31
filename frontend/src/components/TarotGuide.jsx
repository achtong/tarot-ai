import React from "react";

export default function TarotGuide({ step }) {
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
      default:
        return "";
    }
  };

  return <div className="guide-text">{getGuideMessage()}</div>;
}
