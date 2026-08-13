package com.ufu.domain.cauldron.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class CauldronRecombineInsufficientItemQuantityException extends BusinessException {
    public static final CauldronRecombineInsufficientItemQuantityException EXCEPTION =
            new CauldronRecombineInsufficientItemQuantityException();

    private CauldronRecombineInsufficientItemQuantityException() {
        super(ErrorCode.CAULDRON_RECOMBINE_INSUFFICIENT_ITEM_QUANTITY);
    }
}
