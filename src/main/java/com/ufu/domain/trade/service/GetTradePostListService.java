package com.ufu.domain.trade.service;

import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.domain.TradePostStatus;
import com.ufu.domain.trade.presentation.dto.response.TradePostSummaryResponse;
import com.ufu.domain.trade.repository.TradePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTradePostListService {
    private final TradePostRepository tradePostRepository;
    private final TradePostSupport tradePostSupport;

    @Transactional(readOnly = true)
    public List<TradePostSummaryResponse> execute() {
        List<TradePost> tradePosts = tradePostRepository
                .findAllWithAuthorByStatusOrderByCreatedAtDesc(TradePostStatus.OPEN);

        return tradePostSupport.toSummaryResponses(tradePosts);
    }
}
