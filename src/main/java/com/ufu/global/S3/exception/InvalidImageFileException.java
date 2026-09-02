package com.ufu.global.S3.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class InvalidImageFileException extends BusinessException {
    public static final InvalidImageFileException EXCEPTION = new InvalidImageFileException();

    private InvalidImageFileException() {
        super(ErrorCode.INVALID_IMAGE_FILE);
    }
}
