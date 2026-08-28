package com.tarot.demo.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    CARD_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "CARD_NOT_FOUND",
        "카드를 찾을 수 없습니다."
    ),

    COUPON_SOLD_OUT(
        HttpStatus.CONFLICT,
        "COUPON_SOLD_OUT",
        "쿠폰 재고가 모두 소진되었습니다."
    ),

    INVALID_REQUEST(
        HttpStatus.BAD_REQUEST,
        "INVALID_REQUEST",
        "잘못된 요청입니다."
    ),

    INTERNAL_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR",
        "서버 내부 오류가 발생했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
    
}
