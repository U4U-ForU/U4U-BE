package com.ufu.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청")
public class SignupRequest {
    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문과 숫자만 사용할 수 있습니다")
    @Schema(description = "로그인 아이디", example = "test123")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하로 입력해주세요")
    @Pattern(regexp = "^(?!.*[ㄱ-ㅎㅏ-ㅣ가-힣])\\S+$", message = "비밀번호에는 한글과 공백을 포함할 수 없습니다")
    @Schema(description = "비밀번호", example = "12345678")
    private String password;

    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일은 필수입니다")
    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요")
    @Schema(description = "닉네임", example = "테스트")
    private String nickname;
}
