package com.ufu.domain.user.service;

import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.presentation.dto.request.NicknameUpdateRequest;
import com.ufu.domain.user.presentation.dto.response.MyProfileResponse;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMyNicknameService {
    private final UserRepository userRepository;

    @Transactional
    public MyProfileResponse execute(Long userId, NicknameUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        user.changeNickname(request.getNickname());

        return new MyProfileResponse(user);
    }
}
