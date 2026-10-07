package com.ufu.domain.user.service;

import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.AttendanceAlreadyCheckedException;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.presentation.dto.response.AttendanceResponse;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class CheckAttendanceService {
    // "하루"의 기준. 서버 위치와 무관하게 한국 시간 00시에 날이 바뀐다.
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final UserRepository userRepository;

    @Transactional
    public AttendanceResponse execute(Long userId) {
        // 같은 유저의 동시 요청이 둘 다 "오늘 안 함"을 보지 못하도록 행을 잠근다.
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        LocalDate today = LocalDate.now(ZONE);

        if (user.hasAttendedOn(today)) {
            throw AttendanceAlreadyCheckedException.EXCEPTION;
        }

        user.attend(today);
        int reward = user.currentAttendanceReward();
        user.increaseCurrency(reward);

        return new AttendanceResponse(user, reward);
    }
}
