package com.ufu.domain.item.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class ItemSubmissionNotPendingException extends BusinessException {
    public static final ItemSubmissionNotPendingException EXCEPTION = new ItemSubmissionNotPendingException();

    private ItemSubmissionNotPendingException() {
        super(ErrorCode.ITEM_SUBMISSION_NOT_PENDING);
    }
}
