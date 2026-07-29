package com.ufu.domain.itemsubmission.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class ItemSubmissionNotRejectableException extends BusinessException {
    public static final ItemSubmissionNotRejectableException EXCEPTION = new ItemSubmissionNotRejectableException();

    private ItemSubmissionNotRejectableException() {
        super(ErrorCode.ITEM_SUBMISSION_NOT_REJECTABLE);
    }
}
