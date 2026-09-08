package com.ufu.domain.item.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import lombok.Getter;

@Getter
public class AdminItemSummaryResponse {
    private final String itemId;

    private final String name;

    private final String imageUrl;

    private final ItemStatus status;

    public AdminItemSummaryResponse(Item item) {
        this.itemId = item.getItemId();
        this.name = item.getName();
        this.imageUrl = item.getImageUrl();
        this.status = item.getStatus();
    }
}
