package com.ufu.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "로그인 아이디는 비어있을 수 없습니다.")
    @Size(min = 4, max = 20, message = "로그인 아이디는 영문, 숫자 조합 4~20자로 입력해 주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자로 입력해 주세요.")
    @Pattern(regexp = "^(?!.*[ㄱ-ㅎㅏ-ㅣ가-힣])\\S+$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자로 입력해 주세요.")
    private String password;
}
