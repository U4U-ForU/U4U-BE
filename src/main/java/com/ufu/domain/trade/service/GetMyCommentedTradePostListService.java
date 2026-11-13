package com.ufu.domain.trade.service;

import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.presentation.dto.response.TradePostSummaryResponse;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyCommentedTradePostListService {
    private final TradeCommentRepository tradeCommentRepository;
    private final TradePostSupport tradePostSupport;

    @Transactional(readOnly = true)
    public List<TradePostSummaryResponse> execute(Long userId) {
        List<TradePost> tradePosts = tradeCommentRepository
                .findAllWithTradePostAndAuthorByAuthorIdAndStatusOrderByCreatedAtDesc(userId, TradeCommentStatus.PENDING)
                .stream()
                .map(TradeComment::getTradePost)
                .filter(TradePost::isOpen)
                .toList();

        return tradePostSupport.toSummaryResponses(tradePosts);
    }
}
