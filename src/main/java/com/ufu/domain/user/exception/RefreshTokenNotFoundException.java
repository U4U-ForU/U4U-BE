package com.ufu.domain.user.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class RefreshTokenNotFoundException extends BusinessException {
    public static final RefreshTokenNotFoundException EXCEPTION = new RefreshTokenNotFoundException();

    public RefreshTokenNotFoundException() {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
}
