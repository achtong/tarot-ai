package com.tarot.demo.DTO;

import lombok.Data;

@Data
public class SelectedCard {
    private TarotCardDTO card;  // 카드 DTO
    private String position;    // 현재, 장애물, 미래 구분
    private String cardCode;    // 예: "MAJOR_00"
}
