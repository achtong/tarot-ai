package com.tarot.demo.tarot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tarot.demo.tarot.dto.TarotCardDTO;

@Mapper
public interface TarotMapper {
    List<TarotCardDTO> selectAllCards();
}
