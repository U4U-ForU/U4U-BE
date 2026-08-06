package com.ufu.domain.trade.repository;

import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.domain.TradePostStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TradePostRepository extends JpaRepository<TradePost, Long> {
    List<TradePost> findAllByStatusOrderByCreatedAtDesc(TradePostStatus status);

    List<TradePost> findAllByAuthorIdAndStatusOrderByCreatedAtDesc(Long authorId, TradePostStatus status);

    Optional<TradePost> findByTradeId(String tradeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select tradePost from TradePost tradePost where tradePost.tradeId = :tradeId")
    Optional<TradePost> findByTradeIdForUpdate(@Param("tradeId") String tradeId);
}
