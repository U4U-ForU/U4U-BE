package com.ufu.domain.item.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "관리자 아이템 목록 응답")
public class AdminItemSummaryResponse {
    @Schema(description = "아이템 식별값")
    private final String itemId;

    @Schema(description = "아이템 이름")
    private final String name;

    @Schema(description = "아이템 이미지 URL")
    private final String imageUrl;

    @Schema(description = "아이템 상태", example = "COMBINATION")
    private final ItemStatus status;

    public AdminItemSummaryResponse(Item item) {
        this.itemId = item.getItemId();
        this.name = item.getName();
        this.imageUrl = item.getImageUrl();
        this.status = item.getStatus();
    }
}
