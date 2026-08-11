package com.ufu.domain.cauldron.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class CauldronRecipeResultItemInvalidException extends BusinessException {
    public static final CauldronRecipeResultItemInvalidException EXCEPTION = new CauldronRecipeResultItemInvalidException();

    private CauldronRecipeResultItemInvalidException() {
        super(ErrorCode.CAULDRON_RECIPE_RESULT_ITEM_INVALID);
    }
}
