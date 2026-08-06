package com.ufu.domain.item.presentation.dto.response;

import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MyTradingItemGroupResponse {
    private final String tradeType;
    private final String tradeId;
    private final String title;
    private final List<TradeItemResponse> items;
    private final LocalDateTime createdAt;

    public MyTradingItemGroupResponse(
            String tradeType,
            String tradeId,
            String title,
            List<TradeItemResponse> items,
            LocalDateTime createdAt
    ) {
        this.tradeType = tradeType;
        this.tradeId = tradeId;
        this.title = title;
        this.items = items;
        this.createdAt = createdAt;
    }
}
