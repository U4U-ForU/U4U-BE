package com.ufu.domain.trade.repository;

import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TradeCommentRepository extends JpaRepository<TradeComment, Long> {
    @Query("""
            select tradeComment
            from TradeComment tradeComment
            join fetch tradeComment.author
            where tradeComment.tradePost.id = :tradePostId
              and tradeComment.status = :status
            order by tradeComment.createdAt asc
            """)
    List<TradeComment> findAllByTradePostIdAndStatusOrderByCreatedAtAsc(
            @Param("tradePostId") Long tradePostId,
            @Param("status") TradeCommentStatus status
    );

    @Query("""
            select tradeComment.tradePost.id as tradePostId,
                   count(tradeComment) as commentCount
            from TradeComment tradeComment
            where tradeComment.tradePost.id in :tradePostIds
              and tradeComment.status = :status
            group by tradeComment.tradePost.id
            """)
    List<TradeCommentCountProjection> countByTradePostIdInAndStatus(
            @Param("tradePostIds") List<Long> tradePostIds,
            @Param("status") TradeCommentStatus status
    );

    boolean existsByTradePostIdAndAuthorIdAndStatus(Long tradePostId, Long authorId, TradeCommentStatus status);

    @Query("""
            select tradeComment
            from TradeComment tradeComment
            join fetch tradeComment.tradePost
            where tradeComment.author.id = :authorId
              and tradeComment.status = :status
            order by tradeComment.createdAt desc
            """)
    List<TradeComment> findAllWithTradePostByAuthorIdAndStatusOrderByCreatedAtDesc(
            @Param("authorId") Long authorId,
            @Param("status") TradeCommentStatus status
    );

    Optional<TradeComment> findByCommentId(String commentId);
}
