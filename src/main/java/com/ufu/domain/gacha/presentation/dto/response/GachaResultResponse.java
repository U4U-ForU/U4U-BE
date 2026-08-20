package com.ufu.domain.gacha.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "아이템 뽑기 결과 항목")
public class GachaResultResponse {
    @Schema(description = "아이템 식별값", example = "31f0a67a-85c5-4ec9-bc26-7464480f73e0")
    private final String itemId;

    @Schema(description = "아이템 이름", example = "우주 고양이")
    private final String name;

    @Schema(description = "아이템 이미지 URL", example = "https://example-bucket.s3.ap-northeast-2.amazonaws.com/items/example.png")
    private final String imageUrl;

    public GachaResultResponse(Item item) {
        this.itemId = item.getItemId();
        this.name = item.getName();
        this.imageUrl = item.getImageUrl();
    }
}
