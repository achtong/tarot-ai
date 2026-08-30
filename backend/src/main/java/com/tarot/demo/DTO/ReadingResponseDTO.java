package com.tarot.demo.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingResponseDTO {
    private String readingId;
    private List<TarotCardDTO> cards;
}
