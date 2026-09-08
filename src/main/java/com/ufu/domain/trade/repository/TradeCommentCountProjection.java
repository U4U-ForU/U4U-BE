package com.ufu.domain.trade.repository;

public interface TradeCommentCountProjection {
    Long getTradePostId();

    Long getCommentCount();
}
