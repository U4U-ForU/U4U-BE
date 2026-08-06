package com.ufu.domain.trade.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class TradeForbiddenException extends BusinessException {
    public static final TradeForbiddenException EXCEPTION = new TradeForbiddenException();

    private TradeForbiddenException() {
        super(ErrorCode.TRADE_FORBIDDEN);
    }
}
