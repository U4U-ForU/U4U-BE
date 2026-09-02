package com.ufu.domain.auth.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청")
public class LoginRequest {
    @NotBlank(message = "로그인 아이디는 비어있을 수 없습니다.")
    @Size(min = 4, max = 20, message = "로그인 아이디는 영문, 숫자 조합 4~20자로 입력해 주세요.")
    @Schema(description = "로그인 아이디", example = "test123")
    private String loginId;

    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자로 입력해 주세요.")
    @Pattern(regexp = "^(?!.*[ㄱ-ㅎㅏ-ㅣ가-힣])\\S+$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자로 입력해 주세요.")
    @Schema(description = "비밀번호", example = "12345678")
    private String password;
}
