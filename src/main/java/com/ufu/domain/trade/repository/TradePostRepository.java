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
    @Query("""
            select tradePost
            from TradePost tradePost
            join fetch tradePost.author
            where tradePost.status = :status
            order by tradePost.createdAt desc
            """)
    List<TradePost> findAllWithAuthorByStatusOrderByCreatedAtDesc(@Param("status") TradePostStatus status);

    List<TradePost> findAllByAuthorIdAndStatusOrderByCreatedAtDesc(Long authorId, TradePostStatus status);

    @Query("""
            select tradePost
            from TradePost tradePost
            join fetch tradePost.author
            where tradePost.tradeId = :tradeId
            """)
    Optional<TradePost> findByTradeId(@Param("tradeId") String tradeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select tradePost from TradePost tradePost where tradePost.tradeId = :tradeId")
    Optional<TradePost> findByTradeIdForUpdate(@Param("tradeId") String tradeId);
}
