package com.ufu.domain.gacha.service;

import com.ufu.domain.gacha.presentation.dto.response.GachaCurrencyResponse;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetGachaCurrencyService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GachaCurrencyResponse execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        return new GachaCurrencyResponse(user);
    }
}
