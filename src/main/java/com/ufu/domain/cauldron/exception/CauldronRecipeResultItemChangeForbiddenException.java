package com.ufu.domain.cauldron.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class CauldronRecipeResultItemChangeForbiddenException extends BusinessException {
    public static final CauldronRecipeResultItemChangeForbiddenException EXCEPTION =
            new CauldronRecipeResultItemChangeForbiddenException();

    private CauldronRecipeResultItemChangeForbiddenException() {
        super(ErrorCode.CAULDRON_RECIPE_RESULT_ITEM_CHANGE_FORBIDDEN);
    }
}
