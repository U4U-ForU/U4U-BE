package com.ufu.domain.item.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class ItemNotFoundException extends BusinessException {
    public static final ItemNotFoundException EXCEPTION = new ItemNotFoundException();

    private ItemNotFoundException() {
        super(ErrorCode.ITEM_NOT_FOUND);
    }
}
