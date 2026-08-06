package com.ufu.domain.trade.domain;

import com.ufu.domain.item.domain.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "trade_comment_item_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeCommentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_comment_item_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_comment_id", nullable = false)
    private TradeComment tradeComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private int quantity;

    @Builder
    private TradeCommentItem(TradeComment tradeComment, Item item, int quantity) {
        this.tradeComment = tradeComment;
        this.item = item;
        this.quantity = quantity;
    }
}
