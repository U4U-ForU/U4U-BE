package com.ufu.domain.user.presentation;

import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.presentation.dto.request.NicknameUpdateRequest;
import com.ufu.domain.user.presentation.dto.response.AttendanceResponse;
import com.ufu.domain.user.presentation.dto.response.MyProfileResponse;
import com.ufu.domain.user.service.CheckAttendanceService;
import com.ufu.domain.user.service.GetMyProfileService;
import com.ufu.domain.user.service.UpdateMyNicknameService;
import com.ufu.global.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {
    private final GetMyProfileService getMyProfileService;
    private final CheckAttendanceService checkAttendanceService;
    private final UpdateMyNicknameService updateMyNicknameService;

    @GetMapping
    public MyProfileResponse getMyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return getMyProfileService.execute(getUserId(customUserDetails));
    }

    @PostMapping("/attendance")
    public AttendanceResponse checkAttendance(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return checkAttendanceService.execute(getUserId(customUserDetails));
    }

    @PatchMapping("/nickname")
    public MyProfileResponse updateNickname(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody NicknameUpdateRequest request
    ) {
        return updateMyNicknameService.execute(getUserId(customUserDetails), request);
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
