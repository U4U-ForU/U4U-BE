package com.ufu.domain.itemsubmission.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class ItemSubmissionReadForbiddenException extends BusinessException {
    public static final ItemSubmissionReadForbiddenException EXCEPTION = new ItemSubmissionReadForbiddenException();

    private ItemSubmissionReadForbiddenException() {
        super(ErrorCode.ITEM_SUBMISSION_READ_FORBIDDEN);
    }
}
