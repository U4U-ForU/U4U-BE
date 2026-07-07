package com.ufu.domain.user.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class RefreshTokenMisMatchException extends BusinessException {
    public static final RefreshTokenMisMatchException EXCEPTION = new RefreshTokenMisMatchException();

    public RefreshTokenMisMatchException() {
        super(ErrorCode.REFRESH_TOKEN_MIS_MATCH);
    }
}
