package com.ufu.domain.trade.repository;

import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TradeCommentRepository extends JpaRepository<TradeComment, Long> {
    List<TradeComment> findAllByTradePostIdAndStatusOrderByCreatedAtAsc(Long tradePostId, TradeCommentStatus status);

    boolean existsByTradePostIdAndAuthorIdAndStatus(Long tradePostId, Long authorId, TradeCommentStatus status);

    List<TradeComment> findAllByAuthorIdAndStatusOrderByCreatedAtDesc(Long authorId, TradeCommentStatus status);

    Optional<TradeComment> findByCommentId(String commentId);
}
