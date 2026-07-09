package com.ufu.global.error.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다"),
    PASSWORD_MIS_MATCH(400, "비밀번호가 일치하지 않습니다"),
    LOGIN_ID_ALREADY_EXIST(409, "이미 존재하는 아이디입니다"),
    EMAIL_ALREADY_EXIST(409, "이미 존재하는 이메일입니다"),
    REFRESH_TOKEN_NOT_FOUND(404, "refreshToken을 찾을 수 없습니다"),
    REFRESH_TOKEN_MIS_MATCH(403, "refreshToken 값이 저장된 값과 일치하지 않습니다"),
    INVALID_IMAGE_FILE(400, "이미지 파일 형식이 올바르지 않습니다"),
    FILE_UPLOAD_FAILED(500, "이미지 파일 저장에 실패했습니다"),
    ITEM_SUBMISSION_NOT_FOUND(404, "아이템 제출 내역을 찾을 수 없습니다"),
    ITEM_SUBMISSION_FORBIDDEN(403, "해당 제출을 취소할 권한이 없습니다"),
    ITEM_SUBMISSION_NOT_PENDING(400, "심사 중인 제출만 취소할 수 있습니다");

    private final int status;
    private final String message;
}
