package com.ufu.domain.trade.presentation.dto.response;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class TradeCompletionResponse {
    private final String tradeId;
    private final String commentId;
    private final LocalDateTime completedAt;

    public TradeCompletionResponse(String tradeId, String commentId, LocalDateTime completedAt) {
        this.tradeId = tradeId;
        this.commentId = commentId;
        this.completedAt = completedAt;
    }
}
