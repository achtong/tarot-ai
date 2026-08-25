package com.tarot.demo.tarot.controller;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tarot.demo.tarot.dto.TarotCardDTO;
import com.tarot.demo.tarot.service.TarotService;
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
