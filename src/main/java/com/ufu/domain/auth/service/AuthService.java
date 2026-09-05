package com.ufu.domain.auth.service;

import com.ufu.domain.auth.presentation.dto.request.LoginRequest;
import com.ufu.domain.auth.presentation.dto.request.RefreshTokenRequest;
import com.ufu.domain.auth.presentation.dto.request.SignupRequest;
import com.ufu.domain.auth.presentation.dto.response.SignupResponse;
import com.ufu.domain.auth.presentation.dto.response.TokenResponse;
import com.ufu.domain.user.domain.RefreshToken;
import com.ufu.domain.user.domain.Role;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.EmailAlreadyExistException;
import com.ufu.domain.user.exception.LoginIdAlreadyExistException;
import com.ufu.domain.user.exception.PasswordMisMatchException;
import com.ufu.domain.user.exception.RefreshTokenMisMatchException;
import com.ufu.domain.user.exception.RefreshTokenNotFoundException;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.RefreshTokenRepository;
import com.ufu.domain.user.repository.UserRepository;
import com.ufu.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw LoginIdAlreadyExistException.EXCEPTION;
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw EmailAlreadyExistException.EXCEPTION;
        }

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        return new SignupResponse(userRepository.save(user));
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw PasswordMisMatchException.EXCEPTION;
        }

        return jwtTokenProvider.generateBothToken(user.getLoginId(), user.getRole());
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(RefreshTokenRequest request) {
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
