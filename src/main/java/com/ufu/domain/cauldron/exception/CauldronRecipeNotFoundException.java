package com.ufu.domain.cauldron.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class CauldronRecipeNotFoundException extends BusinessException {
    public static final CauldronRecipeNotFoundException EXCEPTION = new CauldronRecipeNotFoundException();

    private CauldronRecipeNotFoundException() {
        super(ErrorCode.CAULDRON_RECIPE_NOT_FOUND);
    }
}
