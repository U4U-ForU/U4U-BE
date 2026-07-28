package com.ufu.domain.gacha.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class InsufficientCurrencyException extends BusinessException {
    public static final InsufficientCurrencyException EXCEPTION = new InsufficientCurrencyException();

    private InsufficientCurrencyException() {
        super(ErrorCode.INSUFFICIENT_CURRENCY);
    }
}
