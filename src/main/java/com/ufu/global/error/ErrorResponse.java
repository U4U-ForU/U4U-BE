package com.ufu.global.error;

import com.ufu.global.error.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;

    @Builder
    private ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(int status, String message) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .build();
    }
}
