package com.ufu.domain.trade.presentation;

import com.ufu.domain.trade.presentation.dto.request.TradePostCreateRequest;
import com.ufu.domain.trade.presentation.dto.request.TradePostTitleUpdateRequest;
import com.ufu.domain.trade.presentation.dto.response.TradePostDeleteResponse;
import com.ufu.domain.trade.presentation.dto.response.TradePostDetailResponse;
import com.ufu.domain.trade.presentation.dto.response.TradePostSummaryResponse;
import com.ufu.domain.trade.service.CreateTradePostService;
import com.ufu.domain.trade.service.DeleteTradePostService;
import com.ufu.domain.trade.service.GetTradePostDetailService;
import com.ufu.domain.trade.service.GetTradePostListService;
import com.ufu.domain.trade.service.UpdateTradePostTitleService;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.security.auth.CustomUserDetails;
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

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradePostController {
    private final CreateTradePostService createTradePostService;
    private final GetTradePostListService getTradePostListService;
    private final GetTradePostDetailService getTradePostDetailService;
    private final UpdateTradePostTitleService updateTradePostTitleService;
    private final DeleteTradePostService deleteTradePostService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TradePostDetailResponse createPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody TradePostCreateRequest request
    ) {
        return createTradePostService.execute(getUserId(customUserDetails), request);
    }

    @GetMapping
    public List<TradePostSummaryResponse> getPosts() {
        return getTradePostListService.execute();
    }

    @GetMapping("/{tradeId}")
    public TradePostDetailResponse getPost(@PathVariable String tradeId) {
        return getTradePostDetailService.execute(tradeId);
    }

    @PatchMapping("/{tradeId}/title")
    public TradePostDetailResponse updateTitle(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String tradeId,
            @Valid @RequestBody TradePostTitleUpdateRequest request
    ) {
        return updateTradePostTitleService.execute(getUserId(customUserDetails), tradeId, request);
    }

    @DeleteMapping("/{tradeId}")
    public TradePostDeleteResponse deletePost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String tradeId
    ) {
        return deleteTradePostService.execute(getUserId(customUserDetails), tradeId);
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
