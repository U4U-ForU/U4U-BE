package com.ufu.domain.gacha.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import lombok.Getter;

@Getter
public class GachaResultResponse {
    private final String itemId;

    private final String name;

    private final String imageUrl;

    public GachaResultResponse(Item item) {
        this.itemId = item.getItemId();
        this.name = item.getName();
        this.imageUrl = item.getImageUrl();
    }
}
