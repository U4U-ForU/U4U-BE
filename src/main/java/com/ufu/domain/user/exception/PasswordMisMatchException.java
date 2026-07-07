package com.ufu.domain.user.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class PasswordMisMatchException extends BusinessException {
    public static final PasswordMisMatchException EXCEPTION = new PasswordMisMatchException();

    public PasswordMisMatchException() {
        super(ErrorCode.PASSWORD_MIS_MATCH);
    }
}
