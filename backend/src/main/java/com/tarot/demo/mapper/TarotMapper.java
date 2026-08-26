package com.tarot.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tarot.demo.DTO.TarotCardDTO;

@Mapper
public interface TarotMapper {
    List<TarotCardDTO> selectAllCards();
}
