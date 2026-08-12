package com.ufu.domain.cauldron.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class CauldronRecipeResultItemInMaterialsException extends BusinessException {
    public static final CauldronRecipeResultItemInMaterialsException EXCEPTION =
            new CauldronRecipeResultItemInMaterialsException();

    private CauldronRecipeResultItemInMaterialsException() {
        super(ErrorCode.CAULDRON_RECIPE_RESULT_ITEM_IN_MATERIALS);
    }
}
