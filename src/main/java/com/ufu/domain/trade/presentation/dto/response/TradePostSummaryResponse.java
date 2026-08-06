package com.ufu.domain.trade.presentation.dto.response;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TradePostSummaryResponse {
    private final String tradeId;
    private final String title;
    private final String authorLoginId;
    private final List<TradeItemResponse> items;
    private final int commentCount;
    private final LocalDateTime createdAt;

    public TradePostSummaryResponse(String tradeId, String title, String authorLoginId, List<TradeItemResponse> items, int commentCount, LocalDateTime createdAt) {
        this.tradeId = tradeId;
        this.title = title;
        this.authorLoginId = authorLoginId;
        this.items = items;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
    }
}
