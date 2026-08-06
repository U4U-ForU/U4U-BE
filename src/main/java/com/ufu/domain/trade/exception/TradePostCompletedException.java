package com.ufu.domain.trade.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class TradePostCompletedException extends BusinessException {
    public static final TradePostCompletedException EXCEPTION = new TradePostCompletedException();

    private TradePostCompletedException() {
        super(ErrorCode.TRADE_POST_COMPLETED);
    }
}
