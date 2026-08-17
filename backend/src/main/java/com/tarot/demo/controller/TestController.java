package com.tarot.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.DTO.CouponIssueDTO;
import com.tarot.demo.service.TestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("test")
     public List<CouponDTO> findAll() {
        return testService.findAll();
    }

    @PostMapping("/coupons/{couponCode}/issue")
    public String issue(
        @PathVariable String couponCode,
        @RequestBody CouponIssueDTO DTO) {
            
            testService.coupon(DTO, couponCode);
    
    return "발급 성공";
}
}