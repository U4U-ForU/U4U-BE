package com.ufu.domain.item.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class InventoryItemNotFoundException extends BusinessException {
    public static final InventoryItemNotFoundException EXCEPTION = new InventoryItemNotFoundException();

    private InventoryItemNotFoundException() {
        super(ErrorCode.INVENTORY_ITEM_NOT_FOUND);
    }
}
