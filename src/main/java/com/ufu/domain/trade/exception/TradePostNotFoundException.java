package com.ufu.domain.trade.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class TradePostNotFoundException extends BusinessException {
    public static final TradePostNotFoundException EXCEPTION = new TradePostNotFoundException();

    private TradePostNotFoundException() {
        super(ErrorCode.TRADE_POST_NOT_FOUND);
    }
}
