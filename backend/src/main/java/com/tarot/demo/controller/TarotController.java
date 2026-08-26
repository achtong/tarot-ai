package com.tarot.demo.controller;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tarot.demo.DTO.TarotCardDTO;
import com.tarot.demo.service.TarotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tarot/")
public class TarotController {
    private final TarotService tarotService;

    @GetMapping("card")
    public List<TarotCardDTO> selectAllCards() {
        return tarotService.selectAllCards();
    }

    @GetMapping("3card")
    public List<TarotCardDTO> select3Cards() {
        return tarotService.select3Cards();
    }
}
