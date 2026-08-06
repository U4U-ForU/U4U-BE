package com.ufu.domain.trade.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class TradeCommentCreateRequest {
    @NotEmpty(message = "아이템은 1개 이상 선택해야 합니다")
    private List<@Valid TradeItemRequest> items;
}
