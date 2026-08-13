package com.ufu.domain.cauldron.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class CauldronMixInsufficientItemQuantityException extends BusinessException {
    public static final CauldronMixInsufficientItemQuantityException EXCEPTION =
            new CauldronMixInsufficientItemQuantityException();

    private CauldronMixInsufficientItemQuantityException() {
        super(ErrorCode.CAULDRON_MIX_INSUFFICIENT_ITEM_QUANTITY);
    }
}
