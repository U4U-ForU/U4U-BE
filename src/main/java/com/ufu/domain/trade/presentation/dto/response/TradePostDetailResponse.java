package com.ufu.domain.trade.presentation.dto.response;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TradePostDetailResponse {
    private final String tradeId;
    private final String title;
    private final String authorLoginId;
    private final List<TradeItemResponse> items;
    private final List<TradeCommentResponse> comments;
    private final LocalDateTime createdAt;

    public TradePostDetailResponse(String tradeId, String title, String authorLoginId, List<TradeItemResponse> items, List<TradeCommentResponse> comments, LocalDateTime createdAt) {
        this.tradeId = tradeId;
        this.title = title;
        this.authorLoginId = authorLoginId;
        this.items = items;
        this.comments = comments;
        this.createdAt = createdAt;
    }
}
