package com.tarot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tarot.demo.DTO.CouponDTO;
import com.tarot.demo.DTO.CouponIssueDTO;
import com.tarot.demo.mapper.TestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {
     private final TestMapper testMapper;

    public List<CouponDTO> findAll() {
        return testMapper.findAll();
    }

    @Transactional
    public boolean coupon(CouponIssueDTO DTO, String CouponCode){
        long start = System.currentTimeMillis();
        int update = testMapper.updateCouponStock(CouponCode);

        if(update == 0){
            long end = System.currentTimeMillis();
            log.info("품절 처리 시간: {} ms",end - start);
            return false;
        }
        DTO.setCouponCode(CouponCode); 
        testMapper.coupon(DTO); 
        long end = System.currentTimeMillis();
        log.info("쿠폰 발급 처리 시간: {} ms", end - start);
        return true;
    }

}
