package com.tarot.demo.DTO;

import lombok.Data;

@Data
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