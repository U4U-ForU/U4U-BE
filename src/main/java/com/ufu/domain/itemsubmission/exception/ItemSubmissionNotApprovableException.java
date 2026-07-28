package com.ufu.domain.itemsubmission.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class ItemSubmissionNotApprovableException extends BusinessException {
    public static final ItemSubmissionNotApprovableException EXCEPTION = new ItemSubmissionNotApprovableException();

    private ItemSubmissionNotApprovableException() {
        super(ErrorCode.ITEM_SUBMISSION_NOT_APPROVABLE);
    }
}
