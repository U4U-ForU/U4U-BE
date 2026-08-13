package com.ufu.domain.cauldron.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class CauldronMixResultItemNotAvailableException extends BusinessException {
    public static final CauldronMixResultItemNotAvailableException EXCEPTION =
            new CauldronMixResultItemNotAvailableException();

    private CauldronMixResultItemNotAvailableException() {
        super(ErrorCode.CAULDRON_MIX_RESULT_ITEM_NOT_AVAILABLE);
    }
}
