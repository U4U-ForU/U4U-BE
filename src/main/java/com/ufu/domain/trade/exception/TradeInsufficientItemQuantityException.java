package com.ufu.domain.trade.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class TradeInsufficientItemQuantityException extends BusinessException {
    public static final TradeInsufficientItemQuantityException EXCEPTION = new TradeInsufficientItemQuantityException();

    private TradeInsufficientItemQuantityException() {
        super(ErrorCode.TRADE_INSUFFICIENT_ITEM_QUANTITY);
    }
}
