package com.tarot.demo.DTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "쿠폰 관련 DTO")
public class CouponDTO {
    @Schema(description = "쿠폰 코드", example = "C001")
    private String couponCode;      // 쿠폰 코드
    @Schema(description = "쿠폰 이름", example = "피자 1판 무료")
    private String couponName;      // 쿠폰 이름
    @Schema(description = "쿠폰 재고", example = "100")
    private int couponStock;        // 재고
}
