package com.ufu.domain.itemsubmission.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class ItemSubmissionForbiddenException extends BusinessException {
    public static final ItemSubmissionForbiddenException EXCEPTION = new ItemSubmissionForbiddenException();

    private ItemSubmissionForbiddenException() {
        super(ErrorCode.ITEM_SUBMISSION_FORBIDDEN);
    }
}
