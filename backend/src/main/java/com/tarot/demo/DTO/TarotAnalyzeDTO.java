package com.tarot.demo.DTO;

import lombok.Data;

@Data
public class TarotAnalyzeDTO {
    private String readingId;
    private String category;      // LOVE / MONEY / CAREER
    private String concern;       // 사용자 고민
    private TarotCardDTO main;
    private TarotCardDTO sub1;
    private TarotCardDTO sub2;
}
