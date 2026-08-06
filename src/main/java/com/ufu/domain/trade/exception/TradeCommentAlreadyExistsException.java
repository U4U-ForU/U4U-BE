package com.ufu.domain.trade.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class TradeCommentAlreadyExistsException extends BusinessException {
    public static final TradeCommentAlreadyExistsException EXCEPTION = new TradeCommentAlreadyExistsException();

    private TradeCommentAlreadyExistsException() {
        super(ErrorCode.TRADE_COMMENT_ALREADY_EXISTS);
    }
}
