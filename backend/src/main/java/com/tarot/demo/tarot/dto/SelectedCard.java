package com.tarot.demo.tarot.dto;

import lombok.Data;

@Data
public class SelectedCard {
    private TarotCardDTO card;  // 카드 DTO
    private String position;    // 현재, 장애물, 미래 구분
}
