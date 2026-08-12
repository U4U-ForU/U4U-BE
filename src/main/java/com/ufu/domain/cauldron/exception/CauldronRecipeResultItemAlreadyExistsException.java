package com.ufu.domain.cauldron.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class CauldronRecipeResultItemAlreadyExistsException extends BusinessException {
    public static final CauldronRecipeResultItemAlreadyExistsException EXCEPTION =
            new CauldronRecipeResultItemAlreadyExistsException();

    private CauldronRecipeResultItemAlreadyExistsException() {
        super(ErrorCode.CAULDRON_RECIPE_RESULT_ITEM_ALREADY_EXISTS);
    }
}
