package com.ufu.domain.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NicknameUpdateRequest {
    @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자만 허용됩니다.")
    private String nickname;
}
