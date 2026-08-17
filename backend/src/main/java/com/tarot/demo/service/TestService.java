package com.tarot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.DTO.CouponIssueDTO;
import com.tarot.demo.mapper.TestMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {
     private final TestMapper testMapper;

    public List<CouponDTO> findAll() {
        return testMapper.findAll();
    }

    public void coupon(CouponIssueDTO DTO, String CouponCode){
        DTO.setCouponCode(CouponCode);
        testMapper.coupon(DTO);
    }

}
