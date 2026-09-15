package com.ufu.domain.item.service;

import com.ufu.domain.item.presentation.dto.response.MyTradingItemGroupResponse;
import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.domain.TradePostStatus;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.repository.TradeCommentItemRepository;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import com.ufu.domain.trade.repository.TradePostItemRepository;
import com.ufu.domain.trade.repository.TradePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GetMyTradingItemListService {
    private final TradePostRepository tradePostRepository;
    private final TradePostItemRepository tradePostItemRepository;
    private final TradeCommentRepository tradeCommentRepository;
    private final TradeCommentItemRepository tradeCommentItemRepository;

    @Transactional(readOnly = true)
    public List<MyTradingItemGroupResponse> execute(Long userId) {
        List<TradePost> tradePosts = tradePostRepository
                .findAllByAuthorIdAndStatusOrderByCreatedAtDesc(userId, TradePostStatus.OPEN);
        List<TradeComment> tradeComments = tradeCommentRepository
                .findAllWithTradePostByAuthorIdAndStatusOrderByCreatedAtDesc(userId, TradeCommentStatus.PENDING)
                .stream()
                .filter(comment -> comment.getTradePost().isOpen())
                .toList();

        Map<Long, List<TradeItemResponse>> itemsByPostId = getItemResponsesByPostId(tradePosts);
        Map<Long, List<TradeItemResponse>> itemsByCommentId = getItemResponsesByCommentId(tradeComments);

        Stream<MyTradingItemGroupResponse> posts = tradePosts.stream()
                .map(tradePost -> new MyTradingItemGroupResponse(
                        "POST",
                        tradePost.getTradeId(),
                        tradePost.getTitle(),
                        itemsByPostId.getOrDefault(tradePost.getId(), List.of()),
                        tradePost.getCreatedAt()
                ));

        Stream<MyTradingItemGroupResponse> comments = tradeComments.stream()
                .map(tradeComment -> new MyTradingItemGroupResponse(
                        "COMMENT",
                        tradeComment.getTradePost().getTradeId(),
                        tradeComment.getTradePost().getTitle(),
                        itemsByCommentId.getOrDefault(tradeComment.getId(), List.of()),
                        tradeComment.getCreatedAt()
                ));

        return Stream.concat(posts, comments)
                .sorted(Comparator.comparing(MyTradingItemGroupResponse::getCreatedAt).reversed())
                .toList();
    }

    private Map<Long, List<TradeItemResponse>> getItemResponsesByPostId(List<TradePost> tradePosts) {
        if (tradePosts.isEmpty()) {
            return Map.of();
        }

        return tradePostItemRepository.findAllWithItemByTradePostIdIn(
                        tradePosts.stream()
                                .map(TradePost::getId)
                                .toList()
                )
                .stream()
                .collect(Collectors.groupingBy(
                        tradePostItem -> tradePostItem.getTradePost().getId(),
                        Collectors.mapping(
                                item -> new TradeItemResponse(item.getItem(), item.getQuantity()),
                                Collectors.toList()
                        )
                ));
    }

    private Map<Long, List<TradeItemResponse>> getItemResponsesByCommentId(List<TradeComment> tradeComments) {
        if (tradeComments.isEmpty()) {
            return Map.of();
        }

        return tradeCommentItemRepository.findAllWithItemByTradeCommentIdIn(
                        tradeComments.stream()
                                .map(TradeComment::getId)
                                .toList()
                )
                .stream()
                .collect(Collectors.groupingBy(
                        tradeCommentItem -> tradeCommentItem.getTradeComment().getId(),
                        Collectors.mapping(
                                item -> new TradeItemResponse(item.getItem(), item.getQuantity()),
                                Collectors.toList()
                        )
                ));
    }
}
