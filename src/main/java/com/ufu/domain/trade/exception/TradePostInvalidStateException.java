package com.ufu.domain.trade.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class TradePostInvalidStateException extends BusinessException {
    public static final TradePostInvalidStateException EXCEPTION = new TradePostInvalidStateException();

    private TradePostInvalidStateException() {
        super(ErrorCode.TRADE_POST_INVALID_STATE);
    }
}
