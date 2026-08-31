package com.tarot.demo.DTO;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CouponIssueDTO {
    @Schema(description = "seq", example = "81")
    private String id;
    @Schema(description = "유저 id", example = "user0001")
    private String userId;
    @Schema(description = "쿠폰 코드", example = "C001")
    private String couponCode;
    @Schema(description = "발급 시간", example = "2026-07-26 18:01:04")
    private LocalDateTime issuedAt;
}
