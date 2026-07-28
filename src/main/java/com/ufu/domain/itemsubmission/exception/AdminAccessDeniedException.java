package com.ufu.domain.itemsubmission.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class AdminAccessDeniedException extends BusinessException {
    public static final AdminAccessDeniedException EXCEPTION = new AdminAccessDeniedException();

    private AdminAccessDeniedException() {
        super(ErrorCode.ADMIN_ACCESS_DENIED);
    }
}
