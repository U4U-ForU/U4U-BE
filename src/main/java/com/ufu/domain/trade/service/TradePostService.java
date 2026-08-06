package com.ufu.domain.trade.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.domain.TradePostItem;
import com.ufu.domain.trade.domain.TradePostStatus;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.exception.TradeForbiddenException;
import com.ufu.domain.trade.exception.TradePostCompletedException;
import com.ufu.domain.trade.exception.TradePostInvalidStateException;
import com.ufu.domain.trade.exception.TradePostNotFoundException;
import com.ufu.domain.trade.presentation.dto.request.TradePostCreateRequest;
import com.ufu.domain.trade.presentation.dto.request.TradePostTitleUpdateRequest;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.presentation.dto.response.TradePostDetailResponse;
import com.ufu.domain.trade.presentation.dto.response.TradePostSummaryResponse;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import com.ufu.domain.trade.repository.TradePostItemRepository;
import com.ufu.domain.trade.repository.TradePostRepository;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradePostService {
    private final TradePostRepository tradePostRepository;
    private final TradePostItemRepository tradePostItemRepository;
    private final TradeCommentRepository tradeCommentRepository;
    private final TradeCommentService tradeCommentService;
    private final UserRepository userRepository;
    private final TradeTransactionService tradeTransactionService;

    @Transactional
    public TradePostDetailResponse createPost(Long userId, TradePostCreateRequest request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        Map<Item, Integer> reservedItems = tradeTransactionService.reserveItems(userId, request.getItems());

        TradePost tradePost = tradePostRepository.save(TradePost.builder()
                .title(request.getTitle().trim())
                .author(author)
                .build());

        List<TradeItemResponse> itemResponses = reservedItems.entrySet()
                .stream()
                .map(entry -> saveTradePostItem(tradePost, entry.getKey(), entry.getValue()))
                .toList();

        return new TradePostDetailResponse(
                tradePost.getTradeId(),
                tradePost.getTitle(),
                author.getLoginId(),
                itemResponses,
                List.of(),
                tradePost.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<TradePostSummaryResponse> getPosts() {
        return tradePostRepository.findAllByStatusOrderByCreatedAtDesc(TradePostStatus.OPEN)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TradePostDetailResponse getPost(String tradeId) {
        TradePost tradePost = findPost(tradeId);

        if (tradePost.isCompleted()) {
            throw TradePostCompletedException.EXCEPTION;
        }

        if (!tradePost.isOpen()) {
            throw TradePostInvalidStateException.EXCEPTION;
        }

        return toDetailResponse(tradePost);
    }

    @Transactional
    public TradePostDetailResponse updateTitle(
            Long userId,
            String tradeId,
            TradePostTitleUpdateRequest request
    ) {
        TradePost tradePost = findOpenPostForUpdate(tradeId);
        verifyAuthor(tradePost, userId);
        tradePost.changeTitle(request.getTitle().trim());

        return toDetailResponse(tradePost);
    }

    @Transactional
    public void deletePost(Long userId, String tradeId) {
        TradePost tradePost = findOpenPostForUpdate(tradeId);
        verifyAuthor(tradePost, userId);
        tradeTransactionService.releaseItems(tradePost.getAuthor(), getPostItems(tradePost));
        tradeCommentService.deletePendingComments(tradePost, null);
        tradePost.delete();
    }

    private TradeItemResponse saveTradePostItem(TradePost tradePost, Item item, int quantity) {
        TradePostItem tradePostItem = TradePostItem.builder()
                .tradePost(tradePost)
                .item(item)
                .quantity(quantity)
                .build();

        tradePostItemRepository.save(tradePostItem);
        return new TradeItemResponse(item, quantity);
    }

    private TradePost findPost(String tradeId) {
        return tradePostRepository.findByTradeId(tradeId)
                .orElseThrow(() -> TradePostNotFoundException.EXCEPTION);
    }

    private TradePost findOpenPostForUpdate(String tradeId) {
        TradePost tradePost = tradePostRepository.findByTradeIdForUpdate(tradeId)
                .orElseThrow(() -> TradePostNotFoundException.EXCEPTION);

        if (tradePost.isCompleted()) {
            throw TradePostCompletedException.EXCEPTION;
        }

        if (!tradePost.isOpen()) {
            throw TradePostInvalidStateException.EXCEPTION;
        }

        return tradePost;
    }

    private void verifyAuthor(TradePost tradePost, Long userId) {
        if (!tradePost.isWrittenBy(userId)) {
            throw TradeForbiddenException.EXCEPTION;
        }
    }

    private Map<Item, Integer> getPostItems(TradePost tradePost) {
        return tradePostItemRepository.findAllByTradePostId(tradePost.getId())
                .stream()
                .collect(Collectors.toMap(
                        TradePostItem::getItem,
                        TradePostItem::getQuantity,
                        Integer::sum,
                        LinkedHashMap::new
                ));
    }

    private List<TradeItemResponse> getPostItemResponses(TradePost tradePost) {
        return tradePostItemRepository.findAllByTradePostId(tradePost.getId())
                .stream()
                .map(item -> new TradeItemResponse(item.getItem(), item.getQuantity()))
                .toList();
    }

    private TradePostSummaryResponse toSummaryResponse(TradePost tradePost) {
        int commentCount = tradeCommentRepository
                .findAllByTradePostIdAndStatusOrderByCreatedAtAsc(
                        tradePost.getId(),
                        TradeCommentStatus.PENDING
                )
                .size();

        return new TradePostSummaryResponse(
                tradePost.getTradeId(),
                tradePost.getTitle(),
                tradePost.getAuthor().getLoginId(),
                getPostItemResponses(tradePost),
                commentCount,
                tradePost.getCreatedAt()
        );
    }

    private TradePostDetailResponse toDetailResponse(TradePost tradePost) {
        return new TradePostDetailResponse(
                tradePost.getTradeId(),
                tradePost.getTitle(),
                tradePost.getAuthor().getLoginId(),
                getPostItemResponses(tradePost),
                tradeCommentService.getPendingComments(tradePost.getId()),
                tradePost.getCreatedAt()
        );
    }
}
