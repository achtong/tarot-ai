package com.tarot.demo.coupon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.DTO.CouponIssueDTO;
import com.tarot.demo.coupon.service.CouponService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Hidden
public class CouponController {

    private final CouponService couponService;

    @GetMapping("test")
    public List<CouponDTO> findAll() {
        return couponService.findAll();
    }

    @PostMapping("/coupons/{couponCode}/issue")
    public ResponseEntity<?> issue(
            @PathVariable String couponCode,
            @RequestBody CouponIssueDTO DTO) {
        couponService.issueCoupon(DTO, couponCode);

        return ResponseEntity.ok("쿠폰 발급이 완료되었습니다.");
    }
}
