package com.ufu.domain.trade.presentation.dto.response;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class TradePostDeleteResponse {
    private final String tradeId;
    private final LocalDateTime deletedAt;

    public TradePostDeleteResponse(String tradeId, LocalDateTime deletedAt) {
        this.tradeId = tradeId;
        this.deletedAt = deletedAt;
    }
}
