package com.ufu.domain.auth.presentation;

import com.ufu.domain.auth.presentation.dto.request.LoginRequest;
import com.ufu.domain.auth.presentation.dto.request.RefreshTokenRequest;
import com.ufu.domain.auth.presentation.dto.request.SignupRequest;
import com.ufu.domain.auth.presentation.dto.response.SignupResponse;
import com.ufu.domain.auth.presentation.dto.response.TokenResponse;
import com.ufu.domain.auth.service.LoginService;
import com.ufu.domain.auth.service.ReissueTokenService;
import com.ufu.domain.auth.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SignupService signupService;
    private final LoginService loginService;
    private final ReissueTokenService reissueTokenService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return signupService.execute(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return loginService.execute(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return reissueTokenService.execute(request);
    }
}
