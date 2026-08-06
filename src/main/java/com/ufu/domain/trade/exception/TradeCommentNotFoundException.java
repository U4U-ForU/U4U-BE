package com.ufu.domain.trade.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class TradeCommentNotFoundException extends BusinessException {
    public static final TradeCommentNotFoundException EXCEPTION = new TradeCommentNotFoundException();

    private TradeCommentNotFoundException() {
        super(ErrorCode.TRADE_COMMENT_NOT_FOUND);
    }
}
