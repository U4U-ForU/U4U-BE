package com.ufu.domain.itemsubmission.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class ItemSubmissionNotCombinableException extends BusinessException {
    public static final ItemSubmissionNotCombinableException EXCEPTION = new ItemSubmissionNotCombinableException();

    private ItemSubmissionNotCombinableException() {
        super(ErrorCode.ITEM_SUBMISSION_NOT_COMBINABLE);
    }
}
