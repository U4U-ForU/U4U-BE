package com.ufu.domain.user.presentation;

import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.presentation.dto.response.MyProfileResponse;
import com.ufu.domain.user.service.GetMyProfileService;
import com.ufu.global.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {
    private final GetMyProfileService getMyProfileService;

    @GetMapping
    public MyProfileResponse getMyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return getMyProfileService.execute(getUserId(customUserDetails));
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
