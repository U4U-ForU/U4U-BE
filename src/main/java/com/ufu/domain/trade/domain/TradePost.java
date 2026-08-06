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
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trade_post_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradePost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_post_id", nullable = false)
    private Long id;

    @Column(name = "public_trade_post_id", nullable = false, unique = true, length = 36)
    private String tradeId;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TradePostStatus status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private TradePost(String title, User author) {
        this.tradeId = UUID.randomUUID().toString();
        this.title = title;
        this.author = author;
        this.status = TradePostStatus.OPEN;
    }

    public boolean isOpen() {
        return status == TradePostStatus.OPEN;
    }

    public boolean isCompleted() {
        return status == TradePostStatus.COMPLETED;
    }

    public boolean isWrittenBy(Long userId) {
        return author.getId().equals(userId);
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void complete() {
        status = TradePostStatus.COMPLETED;
        completedAt = LocalDateTime.now();
    }

    public void delete() {
        status = TradePostStatus.DELETED;
        deletedAt = LocalDateTime.now();
    }
}
