package com.tarot.demo.tarot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tarot.demo.tarot.dto.TarotCardDTO;
import com.tarot.demo.tarot.mapper.TarotMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TarotService {
    private final TarotMapper tarotMapper;

    public List<TarotCardDTO> selectAllCards() {
        return tarotMapper.selectAllCards();
    }
}
