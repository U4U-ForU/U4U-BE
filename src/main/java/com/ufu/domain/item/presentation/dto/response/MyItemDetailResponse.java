package com.ufu.domain.item.presentation.dto.response;

import com.ufu.domain.item.domain.UserItem;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MyItemDetailResponse {
    private final String itemId;

    private final String name;

    private final String description;

    private final String imageUrl;

    private final int quantity;

    private final int reservedQuantity;

    private final int availableQuantity;

    private final LocalDateTime approvedAt;

    private final String creatorLoginId;

    public MyItemDetailResponse(UserItem userItem) {
        this.itemId = userItem.getItem().getItemId();
        this.name = userItem.getItem().getName();
        this.description = userItem.getItem().getDescription();
        this.imageUrl = userItem.getItem().getImageUrl();
        this.quantity = userItem.getQuantity();
        this.reservedQuantity = userItem.getReservedQuantity();
        this.availableQuantity = userItem.getAvailableQuantity();
        this.approvedAt = userItem.getItem().getApprovedAt();
        this.creatorLoginId = userItem.getItem().getCreator().getLoginId();
    }
}
