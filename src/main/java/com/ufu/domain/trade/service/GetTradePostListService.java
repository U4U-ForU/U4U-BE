package com.ufu.domain.trade.service;

import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.domain.TradePostStatus;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.presentation.dto.response.TradePostSummaryResponse;
import com.ufu.domain.trade.repository.TradeCommentCountProjection;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import com.ufu.domain.trade.repository.TradePostItemRepository;
import com.ufu.domain.trade.repository.TradePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTradePostListService {
    private final TradePostRepository tradePostRepository;
    private final TradePostItemRepository tradePostItemRepository;
    private final TradeCommentRepository tradeCommentRepository;

    @Transactional(readOnly = true)
    public List<TradePostSummaryResponse> execute() {
        List<TradePost> tradePosts = tradePostRepository
                .findAllWithAuthorByStatusOrderByCreatedAtDesc(TradePostStatus.OPEN);

        if (tradePosts.isEmpty()) {
            return List.of();
        }

        List<Long> tradePostIds = tradePosts.stream()
                .map(TradePost::getId)
                .toList();
        Map<Long, List<TradeItemResponse>> itemsByPostId = getItemResponsesByPostId(tradePostIds);
        Map<Long, Long> commentCountsByPostId = getPendingCommentCountsByPostId(tradePostIds);

        return tradePosts.stream()
                .map(tradePost -> new TradePostSummaryResponse(
                        tradePost.getTradeId(),
                        tradePost.getTitle(),
                        tradePost.getAuthor().getLoginId(),
                        itemsByPostId.getOrDefault(tradePost.getId(), List.of()),
                        commentCountsByPostId.getOrDefault(tradePost.getId(), 0L).intValue(),
                        tradePost.getCreatedAt()
                ))
                .toList();
    }

    private Map<Long, List<TradeItemResponse>> getItemResponsesByPostId(List<Long> tradePostIds) {
        return tradePostItemRepository.findAllWithItemByTradePostIdIn(tradePostIds)
                .stream()
                .collect(Collectors.groupingBy(
                        tradePostItem -> tradePostItem.getTradePost().getId(),
                        Collectors.mapping(
                                item -> new TradeItemResponse(item.getItem(), item.getQuantity()),
                                Collectors.toList()
                        )
                ));
    }

    private Map<Long, Long> getPendingCommentCountsByPostId(List<Long> tradePostIds) {
        return tradeCommentRepository
                .countByTradePostIdInAndStatus(tradePostIds, TradeCommentStatus.PENDING)
                .stream()
                .collect(Collectors.toMap(
                        TradeCommentCountProjection::getTradePostId,
                        TradeCommentCountProjection::getCommentCount
                ));
    }
}
