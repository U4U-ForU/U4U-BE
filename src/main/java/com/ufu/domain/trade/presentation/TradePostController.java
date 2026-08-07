package com.ufu.domain.trade.presentation;

import com.ufu.domain.trade.presentation.dto.request.TradePostCreateRequest;
import com.ufu.domain.trade.presentation.dto.request.TradePostTitleUpdateRequest;
import com.ufu.domain.trade.presentation.dto.response.TradePostDeleteResponse;
import com.ufu.domain.trade.presentation.dto.response.TradePostDetailResponse;
import com.ufu.domain.trade.presentation.dto.response.TradePostSummaryResponse;
import com.ufu.domain.trade.service.TradePostService;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Trade Post", description = "거래 게시물 API")
@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradePostController {
    private final TradePostService tradePostService;

    @Operation(summary = "거래 게시물 생성", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "거래 게시물 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 또는 거래 가능 아이템 수량 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TradePostDetailResponse createPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody TradePostCreateRequest request
    ) {
        return tradePostService.createPost(getUserId(customUserDetails), request);
    }

    @GetMapping
    public List<TradePostSummaryResponse> getPosts() {
        return tradePostService.getPosts();
    }

    @GetMapping("/{tradeId}")
    public TradePostDetailResponse getPost(@PathVariable String tradeId) {
        return tradePostService.getPost(tradeId);
    }

    @PatchMapping("/{tradeId}/title")
    public TradePostDetailResponse updateTitle(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String tradeId,
            @Valid @RequestBody TradePostTitleUpdateRequest request
    ) {
        return tradePostService.updateTitle(getUserId(customUserDetails), tradeId, request);
    }

    @DeleteMapping("/{tradeId}")
    public TradePostDeleteResponse deletePost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String tradeId
    ) {
        return tradePostService.deletePost(getUserId(customUserDetails), tradeId);
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
