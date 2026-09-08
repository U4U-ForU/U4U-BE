package com.ufu.domain.cauldron.presentation.dto.response;

import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class CauldronRecombineResponse {
    private final MyItemSummaryResponse resultItem;

    private final List<MyItemSummaryResponse> changedItems;

    public CauldronRecombineResponse(
            MyItemSummaryResponse resultItem,
            List<MyItemSummaryResponse> changedItems
    ) {
        this.resultItem = resultItem;
        this.changedItems = changedItems;
    }
}
