package com.ufu.domain.itemsubmission.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class ItemSubmissionNotFoundException extends BusinessException {
    public static final ItemSubmissionNotFoundException EXCEPTION = new ItemSubmissionNotFoundException();

    private ItemSubmissionNotFoundException() {
        super(ErrorCode.ITEM_SUBMISSION_NOT_FOUND);
    }
}
