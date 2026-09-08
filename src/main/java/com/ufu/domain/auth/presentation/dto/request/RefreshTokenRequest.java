package com.ufu.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "refreshToken 값은 필수입니다")
    private String refreshToken;
}
