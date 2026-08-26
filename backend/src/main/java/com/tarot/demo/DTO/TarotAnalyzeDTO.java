package com.tarot.demo.DTO;

import java.util.List;

import lombok.Data;

@Data
public class TarotAnalyzeDTO {
    private String question;                    // 질문
    private String fortuneType;                 // 운세 종류
    private String spreadType;                  // 분석 종류
    private SelectedCard majorCard;             // 메이저 카드
    private List<SelectedCard> minorCards;      // 서브 카드
}
