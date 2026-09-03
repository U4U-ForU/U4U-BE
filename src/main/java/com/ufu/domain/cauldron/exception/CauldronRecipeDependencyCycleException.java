package com.ufu.domain.cauldron.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class CauldronRecipeDependencyCycleException extends BusinessException {
    public static final CauldronRecipeDependencyCycleException EXCEPTION =
            new CauldronRecipeDependencyCycleException();

    private CauldronRecipeDependencyCycleException() {
        super(ErrorCode.CAULDRON_RECIPE_DEPENDENCY_CYCLE);
    }
}
