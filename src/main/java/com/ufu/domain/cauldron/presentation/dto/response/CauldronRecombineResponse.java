package com.ufu.domain.cauldron.presentation.dto.response;

import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.util.List;

@Getter
@Schema(description = "재조합 실행 응답")
public class CauldronRecombineResponse {
    @Schema(description = "지급된 결과 아이템")
    private final MyItemSummaryResponse resultItem;

    @Schema(description = "수량이 변경된 아이템 목록")
    private final List<MyItemSummaryResponse> changedItems;

    public CauldronRecombineResponse(
            MyItemSummaryResponse resultItem,
            List<MyItemSummaryResponse> changedItems
    ) {
        this.resultItem = resultItem;
        this.changedItems = changedItems;
    }
}
