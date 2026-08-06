package com.ufu.domain.trade.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class TradeItemQuantityException extends BusinessException {
    public static final TradeItemQuantityException EXCEPTION = new TradeItemQuantityException();

    private TradeItemQuantityException() {
        super(ErrorCode.TRADE_INVALID_ITEM_QUANTITY);
    }
}
