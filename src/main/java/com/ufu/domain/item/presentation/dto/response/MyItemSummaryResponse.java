package com.ufu.domain.item.presentation.dto.response;

import com.ufu.domain.item.domain.UserItem;
import lombok.Getter;

@Getter
public class MyItemSummaryResponse {
    private final String itemId;

    private final String name;

    private final String imageUrl;

    private final int quantity;

    private final int reservedQuantity;

    private final int availableQuantity;

    public MyItemSummaryResponse(UserItem userItem) {
        this.itemId = userItem.getItem().getItemId();
        this.name = userItem.getItem().getName();
        this.imageUrl = userItem.getItem().getImageUrl();
        this.quantity = userItem.getQuantity();
        this.reservedQuantity = userItem.getReservedQuantity();
        this.availableQuantity = userItem.getAvailableQuantity();
    }
}
