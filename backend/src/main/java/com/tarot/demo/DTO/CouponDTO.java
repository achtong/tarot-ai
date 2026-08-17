package com.tarot.demo.DTO;
import lombok.Data;

@Data
public class CouponDTO {
    private String couponCode;      // 쿠폰 코드
    private String couponName;      // 쿠폰 이름
    private int couponStock;        // 재고
}