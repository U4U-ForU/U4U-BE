package com.ufu.domain.user.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class EmailAlreadyExistException extends BusinessException {
    public static final EmailAlreadyExistException EXCEPTION = new EmailAlreadyExistException();

    public EmailAlreadyExistException() {
        super(ErrorCode.EMAIL_ALREADY_EXIST);
    }
}
