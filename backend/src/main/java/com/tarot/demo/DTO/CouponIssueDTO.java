package com.tarot.demo.DTO;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CouponIssueDTO {
    private String id;
    private String userId;
    private String couponCode;
    private LocalDateTime issuedAt;
}