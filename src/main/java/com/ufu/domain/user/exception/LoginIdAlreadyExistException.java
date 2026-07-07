package com.ufu.domain.user.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class LoginIdAlreadyExistException extends BusinessException {
    public static final LoginIdAlreadyExistException EXCEPTION = new LoginIdAlreadyExistException();

    public LoginIdAlreadyExistException() {
        super(ErrorCode.LOGIN_ID_ALREADY_EXIST);
    }
}
