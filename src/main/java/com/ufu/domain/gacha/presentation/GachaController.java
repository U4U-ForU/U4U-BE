package com.ufu.domain.gacha.presentation;

import com.ufu.domain.gacha.presentation.dto.request.GachaPullRequest;
import com.ufu.domain.gacha.presentation.dto.response.GachaCurrencyResponse;
import com.ufu.domain.gacha.presentation.dto.response.GachaPullResponse;
import com.ufu.domain.gacha.service.GachaService;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.error.ErrorResponse;
import com.ufu.global.security.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Gacha", description = "아이템 뽑기 API")
@RestController
@RequestMapping("/api/gacha")
@RequiredArgsConstructor
public class GachaController {
    private final GachaService gachaService;

    @Operation(summary = "보유 재화 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "보유 재화 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/currency")
    public GachaCurrencyResponse getCurrency(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return gachaService.getCurrency(getUserId(customUserDetails));
    }

    @Operation(summary = "아이템 뽑기 실행", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이템 뽑기 성공"),
            @ApiResponse(responseCode = "400", description = "뽑기 횟수, 재화 또는 뽑기 풀 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/pull")
    public GachaPullResponse pull(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody GachaPullRequest request
    ) {
        return gachaService.pull(getUserId(customUserDetails), request.getCount());
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
