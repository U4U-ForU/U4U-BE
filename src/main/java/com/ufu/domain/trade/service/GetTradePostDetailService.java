package com.ufu.domain.trade.service;

import com.ufu.domain.trade.presentation.dto.response.TradePostDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetTradePostDetailService {
    private final TradePostSupport tradePostSupport;

    @Transactional(readOnly = true)
    public TradePostDetailResponse execute(String tradeId) {
        return tradePostSupport.toDetailResponse(tradePostSupport.findOpenPost(tradeId));
    }
}
