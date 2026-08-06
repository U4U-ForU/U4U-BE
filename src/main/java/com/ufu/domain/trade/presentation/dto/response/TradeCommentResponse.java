package com.ufu.domain.trade.presentation.dto.response;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TradeCommentResponse {
    private final String commentId;
    private final String authorLoginId;
    private final List<TradeItemResponse> items;
    private final LocalDateTime createdAt;

    public TradeCommentResponse(String commentId, String authorLoginId, List<TradeItemResponse> items, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.authorLoginId = authorLoginId;
        this.items = items;
        this.createdAt = createdAt;
    }
}
