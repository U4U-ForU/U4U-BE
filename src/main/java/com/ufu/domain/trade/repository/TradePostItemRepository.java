package com.ufu.domain.trade.repository;

import com.ufu.domain.trade.domain.TradePostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradePostItemRepository extends JpaRepository<TradePostItem, Long> {
    List<TradePostItem> findAllByTradePostId(Long tradePostId);
}
