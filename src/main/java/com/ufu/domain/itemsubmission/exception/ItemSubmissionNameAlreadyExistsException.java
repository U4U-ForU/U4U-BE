package com.ufu.domain.itemsubmission.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class ItemSubmissionNameAlreadyExistsException extends BusinessException {
    public static final ItemSubmissionNameAlreadyExistsException EXCEPTION =
            new ItemSubmissionNameAlreadyExistsException();

    private ItemSubmissionNameAlreadyExistsException() {
        super(ErrorCode.ITEM_SUBMISSION_NAME_ALREADY_EXISTS);
    }
}
