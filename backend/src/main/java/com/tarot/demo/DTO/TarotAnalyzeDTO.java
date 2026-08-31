package com.tarot.demo.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "타로 분석 보낼 DTO")
public class TarotAnalyzeDTO {
    @Schema(description = "분석 읽을 ID", example = "1243-asdf-1234")
    private String readingId;
    @Schema(description = "운세", example = "CAREER")
    private String category;      // LOVE / MONEY / CAREER
    @Schema(description = "고민", example = "사용자의 고민")
    private String concern;       // 사용자 고민
    @Schema(description = "메인카드", example = "메인 카드 상세")
    private TarotCardDTO main;
    @Schema(description = "서브카드1", example = "서브 카드 상세1")
    private TarotCardDTO sub1;
    @Schema(description = "서브카드2", example = "서브 카드 상세2")
    private TarotCardDTO sub2;
}
