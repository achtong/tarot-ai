package com.tarot.demo.coupon.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.DTO.CouponIssueDTO;

@Mapper
public interface CouponMapper {
    List<CouponDTO> findAll();
    void coupon(CouponIssueDTO DTO);
    int updateCouponStock(String couponCode);
    int countCoupon(CouponIssueDTO dto);
    int markCouponUsed(
        @Param("couponCode") String couponCode,
        @Param("userId") String userId
    );
    int deleteCouponIssue(
        @Param("couponCode") String couponCode,
        @Param("userId") String userId
    );
}
