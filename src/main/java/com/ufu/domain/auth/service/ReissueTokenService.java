package com.ufu.domain.auth.service;

import com.ufu.domain.auth.presentation.dto.request.RefreshTokenRequest;
import com.ufu.domain.auth.presentation.dto.response.TokenResponse;
import com.ufu.domain.user.domain.RefreshToken;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.RefreshTokenMisMatchException;
import com.ufu.domain.user.exception.RefreshTokenNotFoundException;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.RefreshTokenRepository;
import com.ufu.domain.user.repository.UserRepository;
import com.ufu.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReissueTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public TokenResponse execute(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        jwtTokenProvider.validateToken(refreshToken, JwtTokenProvider.REFRESH_TYPE);

        String loginId = jwtTokenProvider.getSubject(refreshToken);
        RefreshToken savedRefreshToken = refreshTokenRepository.findById(loginId)
                .orElseThrow(() -> RefreshTokenNotFoundException.EXCEPTION);

        if (!savedRefreshToken.getToken().equals(refreshToken)) {
            throw RefreshTokenMisMatchException.EXCEPTION;
        }

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        return jwtTokenProvider.generateBothToken(user.getLoginId(), user.getRole());
    }
}
