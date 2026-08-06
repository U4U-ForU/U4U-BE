package com.ufu.domain.trade.repository;

import com.ufu.domain.trade.domain.TradeCommentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeCommentItemRepository extends JpaRepository<TradeCommentItem, Long> {
    List<TradeCommentItem> findAllByTradeCommentId(Long tradeCommentId);

    void deleteAllByTradeCommentId(Long tradeCommentId);
}
