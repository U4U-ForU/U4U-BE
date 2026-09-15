package com.ufu.domain.trade.presentation;

import com.ufu.domain.trade.presentation.dto.request.TradeCommentCreateRequest;
import com.ufu.domain.trade.presentation.dto.response.TradeCommentResponse;
import com.ufu.domain.trade.presentation.dto.response.TradeCompletionResponse;
import com.ufu.domain.trade.service.CancelTradeCommentService;
import com.ufu.domain.trade.service.CompleteTradeService;
import com.ufu.domain.trade.service.CreateTradeCommentService;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades/{tradeId}/comments")
@RequiredArgsConstructor
public class TradeCommentController {
    private final CreateTradeCommentService createTradeCommentService;
    private final CancelTradeCommentService cancelTradeCommentService;
    private final CompleteTradeService completeTradeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TradeCommentResponse createComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String tradeId,
            @Valid @RequestBody TradeCommentCreateRequest request
    ) {
        return createTradeCommentService.execute(getUserId(customUserDetails), tradeId, request);
    }

    @DeleteMapping("/{commentId}")
    public TradeCommentResponse cancelComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String tradeId,
            @PathVariable String commentId
    ) {
        return cancelTradeCommentService.execute(getUserId(customUserDetails), tradeId, commentId);
    }

    @PatchMapping("/{commentId}/accept")
    public TradeCompletionResponse completeTrade(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String tradeId,
            @PathVariable String commentId
    ) {
        return completeTradeService.execute(getUserId(customUserDetails), tradeId, commentId);
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
