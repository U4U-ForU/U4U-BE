package com.ufu.domain.trade.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentItem;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.exception.TradeCommentAlreadyExistsException;
import com.ufu.domain.trade.exception.TradeCommentNotFoundException;
import com.ufu.domain.trade.exception.TradeForbiddenException;
import com.ufu.domain.trade.exception.TradePostCompletedException;
import com.ufu.domain.trade.exception.TradePostInvalidStateException;
import com.ufu.domain.trade.exception.TradePostNotFoundException;
import com.ufu.domain.trade.presentation.dto.request.TradeCommentCreateRequest;
import com.ufu.domain.trade.presentation.dto.response.TradeCommentResponse;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.repository.TradeCommentItemRepository;
import com.ufu.domain.trade.repository.TradeCommentRepository;
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
public class TradeCommentService {
    private final TradePostRepository tradePostRepository;
    private final TradeCommentRepository tradeCommentRepository;
    private final TradeCommentItemRepository tradeCommentItemRepository;
    private final UserRepository userRepository;
    private final TradeTransactionService tradeTransactionService;

    @Transactional
    public TradeCommentResponse createComment(
            Long userId,
            String tradeId,
            TradeCommentCreateRequest request
    ) {
        TradePost tradePost = findOpenPostForUpdate(tradeId);
        if (tradePost.isWrittenBy(userId)) {
            throw TradeForbiddenException.EXCEPTION;
        }

        if (tradeCommentRepository.existsByTradePostIdAndAuthorIdAndStatus(
                tradePost.getId(), userId, TradeCommentStatus.PENDING)) {
            throw TradeCommentAlreadyExistsException.EXCEPTION;
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        Map<Item, Integer> reservedItems = tradeTransactionService.reserveItems(userId, request.getItems());
        TradeComment tradeComment = tradeCommentRepository.save(TradeComment.builder()
                .tradePost(tradePost)
                .author(author)
                .build());

        List<TradeItemResponse> items = reservedItems.entrySet()
                .stream()
                .map(entry -> saveTradeCommentItem(tradeComment, entry.getKey(), entry.getValue()))
                .toList();

        return new TradeCommentResponse(
                tradeComment.getCommentId(),
                author.getLoginId(),
                items,
                tradeComment.getCreatedAt()
        );
    }

    @Transactional
    public TradeCommentResponse cancelComment(Long userId, String tradeId, String commentId) {
        TradePost tradePost = findOpenPostForUpdate(tradeId);
        TradeComment tradeComment = tradeCommentRepository.findByCommentId(commentId)
                .orElseThrow(() -> TradeCommentNotFoundException.EXCEPTION);

        if (!tradeComment.getTradePost().getId().equals(tradePost.getId())) {
            throw TradeCommentNotFoundException.EXCEPTION;
        }

        if (!tradeComment.isWrittenBy(userId)) {
            throw TradeForbiddenException.EXCEPTION;
        }

        TradeCommentResponse response = toResponse(tradeComment);
        Map<Item, Integer> items = getCommentItems(tradeComment);
        tradeTransactionService.releaseItems(tradeComment.getAuthor(), items);
        tradeCommentItemRepository.deleteAllByTradeCommentId(tradeComment.getId());
        tradeCommentRepository.delete(tradeComment);

        return response;
    }

    @Transactional(readOnly = true)
    public List<TradeCommentResponse> getPendingComments(Long tradePostId) {
        List<TradeComment> comments = tradeCommentRepository
                .findAllByTradePostIdAndStatusOrderByCreatedAtAsc(tradePostId, TradeCommentStatus.PENDING);

        if (comments.isEmpty()) {
            return List.of();
        }

        Map<Long, List<TradeItemResponse>> itemsByCommentId = getItemResponsesByCommentId(
                comments.stream()
                        .map(TradeComment::getId)
                        .toList()
        );

        return comments.stream()
                .map(comment -> new TradeCommentResponse(
                        comment.getCommentId(),
                        comment.getAuthor().getLoginId(),
                        itemsByCommentId.getOrDefault(comment.getId(), List.of()),
                        comment.getCreatedAt()
                ))
                .toList();
    }

    private Map<Long, List<TradeItemResponse>> getItemResponsesByCommentId(List<Long> tradeCommentIds) {
        return tradeCommentItemRepository.findAllWithItemByTradeCommentIdIn(tradeCommentIds)
                .stream()
                .collect(Collectors.groupingBy(
                        tradeCommentItem -> tradeCommentItem.getTradeComment().getId(),
                        Collectors.mapping(
                                item -> new TradeItemResponse(item.getItem(), item.getQuantity()),
                                Collectors.toList()
                        )
                ));
    }

    @Transactional
    public void deletePendingComments(TradePost tradePost, String excludedCommentId) {
        List<TradeComment> comments = tradeCommentRepository.findAllByTradePostIdAndStatusOrderByCreatedAtAsc(
                tradePost.getId(),
                TradeCommentStatus.PENDING
        );

        for (TradeComment comment : comments) {
            if (comment.getCommentId().equals(excludedCommentId)) {
                continue;
            }

            tradeTransactionService.releaseItems(comment.getAuthor(), getCommentItems(comment));
            tradeCommentItemRepository.deleteAllByTradeCommentId(comment.getId());
            tradeCommentRepository.delete(comment);
        }
    }

    private TradeItemResponse saveTradeCommentItem(TradeComment tradeComment, Item item, int quantity) {
        tradeCommentItemRepository.save(TradeCommentItem.builder()
                .tradeComment(tradeComment)
                .item(item)
                .quantity(quantity)
                .build());

        return new TradeItemResponse(item, quantity);
    }

    private Map<Item, Integer> getCommentItems(TradeComment tradeComment) {
        return tradeCommentItemRepository.findAllWithItemByTradeCommentId(tradeComment.getId())
                .stream()
                .collect(Collectors.toMap(
                        TradeCommentItem::getItem,
                        TradeCommentItem::getQuantity,
                        Integer::sum,
                        LinkedHashMap::new
                ));
    }

    private TradeCommentResponse toResponse(TradeComment tradeComment) {
        List<TradeItemResponse> items = tradeCommentItemRepository
                .findAllWithItemByTradeCommentId(tradeComment.getId())
                .stream()
                .map(item -> new TradeItemResponse(item.getItem(), item.getQuantity()))
                .toList();

        return new TradeCommentResponse(
                tradeComment.getCommentId(),
                tradeComment.getAuthor().getLoginId(),
                items,
                tradeComment.getCreatedAt()
        );
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
}
