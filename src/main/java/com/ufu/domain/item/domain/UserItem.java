package com.ufu.domain.item.domain;

import com.ufu.domain.user.domain.User;
import com.ufu.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_item_tbl",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "item_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_item_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Builder
    private UserItem(User user, Item item, int quantity) {
        this.user = user;
        this.item = item;
        this.quantity = quantity;
        this.reservedQuantity = 0;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        this.quantity -= amount;
    }

    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public boolean hasAvailableQuantity(int amount) {
        return amount > 0 && getAvailableQuantity() >= amount;
    }

    public void reserveQuantity(int amount) {
        if (!hasAvailableQuantity(amount)) {
            throw new IllegalArgumentException("예약 가능한 아이템 수량이 부족합니다");
        }

        reservedQuantity += amount;
    }

    public void releaseReservedQuantity(int amount) {
        if (amount <= 0 || reservedQuantity < amount) {
            throw new IllegalArgumentException("예약 해제 수량이 올바르지 않습니다");
        }

        reservedQuantity -= amount;
    }

    public void transferReservedQuantity(int amount) {
        releaseReservedQuantity(amount);
        quantity -= amount;
    }
}
