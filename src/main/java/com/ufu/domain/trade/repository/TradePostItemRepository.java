package com.ufu.domain.trade.repository;

import com.ufu.domain.trade.domain.TradePostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TradePostItemRepository extends JpaRepository<TradePostItem, Long> {
    @Query("""
            select tradePostItem
            from TradePostItem tradePostItem
            join fetch tradePostItem.item
            where tradePostItem.tradePost.id = :tradePostId
            """)
    List<TradePostItem> findAllWithItemByTradePostId(@Param("tradePostId") Long tradePostId);

    @Query("""
            select tradePostItem
            from TradePostItem tradePostItem
            join fetch tradePostItem.item
            where tradePostItem.tradePost.id in :tradePostIds
            """)
    List<TradePostItem> findAllWithItemByTradePostIdIn(@Param("tradePostIds") List<Long> tradePostIds);
}
