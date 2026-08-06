package com.ufu.domain.trade.domain;

import com.ufu.domain.user.domain.User;
import com.ufu.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "trade_comment_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_comment_id", nullable = false)
    private Long id;

    @Column(name = "public_trade_comment_id", nullable = false, unique = true, length = 36)
    private String commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_post_id", nullable = false)
    private TradePost tradePost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TradeCommentStatus status;

    @Builder
    private TradeComment(TradePost tradePost, User author) {
        this.commentId = UUID.randomUUID().toString();
        this.tradePost = tradePost;
        this.author = author;
        this.status = TradeCommentStatus.PENDING;
    }

    public boolean isPending() {
        return status == TradeCommentStatus.PENDING;
    }

    public boolean isWrittenBy(Long userId) {
        return author.getId().equals(userId);
    }

    public void accept() {
        status = TradeCommentStatus.ACCEPTED;
    }
}
