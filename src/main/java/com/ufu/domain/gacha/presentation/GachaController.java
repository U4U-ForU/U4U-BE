package com.ufu.domain.gacha.presentation;

import com.ufu.domain.gacha.presentation.dto.request.GachaPullRequest;
import com.ufu.domain.gacha.presentation.dto.response.GachaCurrencyResponse;
import com.ufu.domain.gacha.presentation.dto.response.GachaPullResponse;
import com.ufu.domain.gacha.service.GetGachaCurrencyService;
import com.ufu.domain.gacha.service.PullGachaService;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gacha")
@RequiredArgsConstructor
public class GachaController {
    private final GetGachaCurrencyService getGachaCurrencyService;
    private final PullGachaService pullGachaService;

    @GetMapping("/currency")
    public GachaCurrencyResponse getCurrency(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return getGachaCurrencyService.execute(getUserId(customUserDetails));
    }

    @PostMapping("/pull")
    public GachaPullResponse pull(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody GachaPullRequest request
    ) {
        return pullGachaService.execute(getUserId(customUserDetails), request.getCount());
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
