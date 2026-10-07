package com.ufu.domain.user.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class AttendanceAlreadyCheckedException extends BusinessException {
    public static final AttendanceAlreadyCheckedException EXCEPTION = new AttendanceAlreadyCheckedException();

    private AttendanceAlreadyCheckedException() {
        super(ErrorCode.ATTENDANCE_ALREADY_CHECKED);
    }
}
