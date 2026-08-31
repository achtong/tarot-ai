package com.tarot.demo.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Redis에서 조회해올 카드")
public class TarotCardDTO {
    private String cardCode;
    private String cardType;
    private String suit;
    private Integer cardNumber;
    private String nameKr;
    private String nameEn;
    private String imageUrl;
    private String keywordsUp;
    private String summary;
}