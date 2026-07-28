package com.ufu.domain.gacha.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class GachaPoolEmptyException extends BusinessException {
    public static final GachaPoolEmptyException EXCEPTION = new GachaPoolEmptyException();

    private GachaPoolEmptyException() {
        super(ErrorCode.GACHA_POOL_EMPTY);
    }
}
