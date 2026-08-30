package com.tarot.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarotReadingDTO {
    private String mainCardCode;
    private String sub1CardCode;
    private String sub2CardCode;
}
