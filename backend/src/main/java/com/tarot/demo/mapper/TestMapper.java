package com.tarot.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.DTO.CouponIssueDTO;

@Mapper
public interface TestMapper {
    List<CouponDTO> findAll();
    void coupon(CouponIssueDTO DTO);
    int updateCouponStock(String couponCode);
}
