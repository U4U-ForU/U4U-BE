package com.ufu.global.error.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User
    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다"),
    PASSWORD_MIS_MATCH(400, "비밀번호가 일치하지 않습니다"),
    LOGIN_ID_ALREADY_EXIST(409, "이미 존재하는 아이디입니다"),
    EMAIL_ALREADY_EXIST(409, "이미 존재하는 이메일입니다"),
    // JWT
    REFRESH_TOKEN_NOT_FOUND(404, "refreshToken을 찾을 수 없습니다"),
    REFRESH_TOKEN_MIS_MATCH(403, "refreshToken 값이 저장된 값과 일치하지 않습니다"),
    // IMG
    INVALID_IMAGE_FILE(400, "이미지 파일 형식이 올바르지 않습니다"),
    FILE_UPLOAD_FAILED(500, "이미지 파일 저장에 실패했습니다"),
    // Item
    INVENTORY_ITEM_NOT_FOUND(404, "보유한 아이템을 찾을 수 없습니다"),
    ITEM_NOT_FOUND(404, "아이템을 찾을 수 없습니다"),
    // ADMIN
    ADMIN_ACCESS_DENIED(403, "관리자만 접근할 수 있습니다"),
    ITEM_SUBMISSION_NOT_APPROVABLE(400, "심사 중인 제출만 승인할 수 있습니다"),
    ITEM_SUBMISSION_NOT_COMBINABLE(400, "심사 중인 제출만 조합 처리할 수 있습니다"),
    ITEM_SUBMISSION_NOT_REJECTABLE(400, "심사 중인 제출만 거절할 수 있습니다"),
    // GACHA
    INSUFFICIENT_CURRENCY(400, "보유 재화가 부족합니다"),
    GACHA_POOL_EMPTY(400, "뽑기 가능한 아이템이 없습니다"),
    // SUBMISSION
    ITEM_SUBMISSION_NOT_FOUND(404, "아이템 제출 내역을 찾을 수 없습니다"),
    ITEM_SUBMISSION_FORBIDDEN(403, "해당 제출을 취소할 권한이 없습니다"),
    ITEM_SUBMISSION_NOT_PENDING(400, "심사 중인 제출만 취소할 수 있습니다"),
    ITEM_SUBMISSION_NAME_ALREADY_EXISTS(409, "이미 사용 중인 아이템 이름입니다"),
    // TRADE-POST
    TRADE_POST_NOT_FOUND(404, "거래 게시물을 찾을 수 없습니다"),
    TRADE_COMMENT_NOT_FOUND(404, "거래 제안을 찾을 수 없습니다"),
    TRADE_POST_COMPLETED(409, "이미 성사된 거래입니다"),
    // TRADE-COMMENT
    TRADE_FORBIDDEN(403, "해당 거래를 처리할 권한이 없습니다"),
    TRADE_SELF_COMMENT_FORBIDDEN(403, "자신의 거래 게시물에는 제안할 수 없습니다"),
    TRADE_COMMENT_ALREADY_EXISTS(409, "이미 활성 거래 제안이 있습니다"),
    TRADE_POST_INVALID_STATE(409, "현재 거래 상태에서는 처리할 수 없습니다"),
    TRADE_INVALID_ITEM_QUANTITY(400, "아이템은 총 1개 이상 10개 이하로 선택해야 합니다"),
    TRADE_INSUFFICIENT_ITEM_QUANTITY(400, "거래 가능한 아이템 수량이 부족합니다"),
    // COMBINATION
    CAULDRON_RECIPE_NOT_FOUND(404, "조합법을 찾을 수 없습니다"),
    CAULDRON_RECIPE_RESULT_ITEM_INVALID(400, "완성 아이템은 조합 아이템이어야 합니다"),
    CAULDRON_RECIPE_RESULT_ITEM_IN_MATERIALS(400, "완성 아이템은 재료 아이템에 포함할 수 없습니다"),
    CAULDRON_RECIPE_RESULT_ITEM_ALREADY_EXISTS(409, "이미 해당 완성 아이템의 활성 조합법이 있습니다"),
    CAULDRON_RECIPE_RESULT_ITEM_CHANGE_FORBIDDEN(400, "조합법의 완성 아이템은 변경할 수 없습니다"),
    CAULDRON_RECOMBINE_INSUFFICIENT_ITEM_QUANTITY(400, "재조합에 필요한 아이템 수량이 부족합니다"),
    // MIX
    CAULDRON_MIX_INSUFFICIENT_ITEM_QUANTITY(400, "섞기에 필요한 아이템 수량이 부족합니다"),
    CAULDRON_MIX_RESULT_ITEM_NOT_AVAILABLE(409, "섞기 결과로 지급할 수 있는 아이템이 없습니다");

    private final int status;
    private final String message;
}
