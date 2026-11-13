package com.ufu.domain.item.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AdminItemListResponse {
    private final String itemId;

    private final String name;

    private final String imageUrl;

    private final ItemStatus status;

    private final String creatorLoginId;

    private final LocalDateTime approvedAt;

    public AdminItemListResponse(Item item) {
        this.itemId = item.getItemId();
        this.name = item.getName();
        this.imageUrl = item.getImageUrl();
        this.status = item.getStatus();
        this.creatorLoginId = item.getCreator().getLoginId();
        this.approvedAt = item.getApprovedAt();
    }
}
