package com.ufu.domain.trade.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import lombok.Getter;

@Getter
public class TradeItemResponse {
    private final String itemId;
    private final String name;
    private final String imageUrl;
    private final int quantity;

    public TradeItemResponse(Item item, int quantity) {
        this.itemId = item.getItemId();
        this.name = item.getName();
        this.imageUrl = item.getImageUrl();
        this.quantity = quantity;
    }
}
