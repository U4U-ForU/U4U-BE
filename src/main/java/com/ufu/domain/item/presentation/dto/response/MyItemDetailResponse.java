package com.ufu.domain.item.presentation.dto.response;

import com.ufu.domain.item.domain.UserItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Schema(description = "내 보유 아이템 상세 응답")
public class MyItemDetailResponse {
    @Schema(description = "아이템 식별값", example = "31f0a67a-85c5-4ec9-bc26-7464480f73e0")
    private final String itemId;

    @Schema(description = "아이템 이름", example = "우주 고양이")
    private final String name;

    @Schema(description = "아이템 설명", example = "별빛을 모으는 고양이 아이템입니다.")
    private final String description;

    @Schema(description = "아이템 이미지 URL", example = "/uploads/example.png")
    private final String imageUrl;

    @Schema(description = "보유 수량", example = "3")
    private final int quantity;

    @Schema(description = "거래 예약 수량", example = "1")
    private final int reservedQuantity;

    @Schema(description = "거래 가능 수량", example = "2")
    private final int availableQuantity;

    @Schema(description = "아이템 승인 시간")
    private final LocalDateTime approvedAt;

    @Schema(description = "크리에이터 로그인 아이디", example = "catmaker")
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
