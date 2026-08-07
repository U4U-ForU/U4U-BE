package com.ufu.domain.trade.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentItem;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.domain.TradePostItem;
import com.ufu.domain.trade.exception.TradeCommentNotFoundException;
import com.ufu.domain.trade.exception.TradeForbiddenException;
import com.ufu.domain.trade.exception.TradePostCompletedException;
import com.ufu.domain.trade.exception.TradePostInvalidStateException;
import com.ufu.domain.trade.exception.TradePostNotFoundException;
import com.ufu.domain.trade.presentation.dto.response.TradeCompletionResponse;
import com.ufu.domain.trade.repository.TradeCommentItemRepository;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import com.ufu.domain.trade.repository.TradePostItemRepository;
import com.ufu.domain.trade.repository.TradePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeCompletionService {
    private final TradePostRepository tradePostRepository;
    private final TradePostItemRepository tradePostItemRepository;
    private final TradeCommentRepository tradeCommentRepository;
    private final TradeCommentItemRepository tradeCommentItemRepository;
    private final TradeTransactionService tradeTransactionService;
    private final TradeCommentService tradeCommentService;

    @Transactional
    public TradeCompletionResponse completeTrade(Long userId, String tradeId, String commentId) {
        TradePost tradePost = findOpenPostForUpdate(tradeId);
        if (!tradePost.isWrittenBy(userId)) {
            throw TradeForbiddenException.EXCEPTION;
        }

        TradeComment acceptedComment = tradeCommentRepository.findByCommentId(commentId)
                .orElseThrow(() -> TradeCommentNotFoundException.EXCEPTION);

        if (!acceptedComment.getTradePost().getId().equals(tradePost.getId()) || !acceptedComment.isPending()) {
            throw TradeCommentNotFoundException.EXCEPTION;
        }

        tradeTransactionService.transferItems(
                tradePost.getAuthor(),
                acceptedComment.getAuthor(),
                getPostItems(tradePost)
        );
        tradeTransactionService.transferItems(
                acceptedComment.getAuthor(),
                tradePost.getAuthor(),
                getCommentItems(acceptedComment)
        );

        acceptedComment.accept();
        tradeCommentService.deletePendingComments(tradePost, acceptedComment.getCommentId());
        tradePost.complete();

        return new TradeCompletionResponse(
                tradePost.getTradeId(),
                acceptedComment.getCommentId(),
                tradePost.getCompletedAt()
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

    private Map<Item, Integer> getPostItems(TradePost tradePost) {
        Map<Item, Integer> items = new LinkedHashMap<>();

        for (TradePostItem item : tradePostItemRepository.findAllByTradePostId(tradePost.getId())) {
            items.merge(item.getItem(), item.getQuantity(), Integer::sum);
        }

        return items;
    }

    private Map<Item, Integer> getCommentItems(TradeComment tradeComment) {
        Map<Item, Integer> items = new LinkedHashMap<>();

        for (TradeCommentItem item : tradeCommentItemRepository.findAllByTradeCommentId(tradeComment.getId())) {
            items.merge(item.getItem(), item.getQuantity(), Integer::sum);
        }

        return items;
    }
}
