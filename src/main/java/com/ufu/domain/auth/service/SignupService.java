package com.ufu.domain.auth.service;

import com.ufu.domain.auth.presentation.dto.request.SignupRequest;
import com.ufu.domain.auth.presentation.dto.response.SignupResponse;
import com.ufu.domain.user.domain.Role;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.EmailAlreadyExistException;
import com.ufu.domain.user.exception.LoginIdAlreadyExistException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse execute(SignupRequest request) {
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
}
