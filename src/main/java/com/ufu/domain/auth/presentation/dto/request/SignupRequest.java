package com.ufu.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank(message = "로그인 아이디는 비어있을 수 없습니다.")
    @Size(min = 4, max = 20, message = "로그인 아이디는 영문, 숫자 조합 4~20자로 입력해 주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자로 입력해 주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])[\\x21-\\x7E]{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함하여 8~20자로 입력해 주세요."
    )
    private String password;

    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요")
    private String nickname;
}
