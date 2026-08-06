package com.ufu.domain.trade.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeItemRequest {
    @NotBlank(message = "아이템 식별값은 필수입니다")
    private String itemId;

    @Min(value = 1, message = "아이템 수량은 1 이상이어야 합니다")
    private int quantity;
}
