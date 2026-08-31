package com.tarot.demo.DTO;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI Agent가 읽을 카드 DTO")
public class ReadingResponseDTO {
    @Schema(description = "분석 읽을 ID", example = "1243-asdf-1234")
    private String readingId;
    @Schema(description = "카드 목록", example = "카드 목록 List")
    private List<TarotCardDTO> cards;
}
