package com.ufu.domain.trade.service;

import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.presentation.dto.request.TradePostTitleUpdateRequest;
import com.ufu.domain.trade.presentation.dto.response.TradePostDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTradePostTitleService {
    private final TradePostSupport tradePostSupport;

    @Transactional
    public TradePostDetailResponse execute(
            Long userId,
            String tradeId,
            TradePostTitleUpdateRequest request
    ) {
        TradePost tradePost = tradePostSupport.findOpenPostForUpdate(tradeId);
        tradePostSupport.verifyAuthor(tradePost, userId);
        tradePost.changeTitle(request.getTitle().trim());

        return tradePostSupport.toDetailResponse(tradePost);
    }
}
