package com.ufu.domain.auth.presentation.dto.response;

import com.ufu.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회원가입 응답")
public class SignupResponse {
    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "로그인 아이디", example = "test123")
    private final String loginId;

    @Schema(description = "이메일", example = "test@example.com")
    private final String email;

    @Schema(description = "닉네임", example = "테스트")
    private final String nickname;

    public SignupResponse(User user) {
        this.userId = user.getId();
        this.loginId = user.getLoginId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}
