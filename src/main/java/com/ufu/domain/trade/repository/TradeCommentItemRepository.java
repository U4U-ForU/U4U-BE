package com.ufu.domain.trade.repository;

import com.ufu.domain.trade.domain.TradeCommentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TradeCommentItemRepository extends JpaRepository<TradeCommentItem, Long> {
    @Query("""
            select tradeCommentItem
            from TradeCommentItem tradeCommentItem
            join fetch tradeCommentItem.item
            where tradeCommentItem.tradeComment.id = :tradeCommentId
            """)
    List<TradeCommentItem> findAllWithItemByTradeCommentId(@Param("tradeCommentId") Long tradeCommentId);

    @Query("""
            select tradeCommentItem
            from TradeCommentItem tradeCommentItem
            join fetch tradeCommentItem.item
            where tradeCommentItem.tradeComment.id in :tradeCommentIds
            """)
    List<TradeCommentItem> findAllWithItemByTradeCommentIdIn(
            @Param("tradeCommentIds") List<Long> tradeCommentIds
    );

    void deleteAllByTradeCommentId(Long tradeCommentId);
}
