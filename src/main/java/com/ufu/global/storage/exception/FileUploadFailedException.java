package com.ufu.global.storage.exception;

import com.ufu.global.error.exception.BusinessException;
import com.ufu.global.error.exception.ErrorCode;

public class FileUploadFailedException extends BusinessException {
    public static final FileUploadFailedException EXCEPTION = new FileUploadFailedException();

    private FileUploadFailedException() {
        super(ErrorCode.FILE_UPLOAD_FAILED);
    }
}
