package com.ufu.domain.trade.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.exception.TradeCommentNotFoundException;
import com.ufu.domain.trade.exception.TradeForbiddenException;
import com.ufu.domain.trade.presentation.dto.response.TradeCommentResponse;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.repository.TradeCommentItemRepository;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CancelTradeCommentService {
    private final TradeCommentRepository tradeCommentRepository;
    private final TradeCommentItemRepository tradeCommentItemRepository;
    private final TradeTransactionService tradeTransactionService;
    private final TradePostSupport tradePostSupport;
    private final TradeCommentSupport tradeCommentSupport;

    @Transactional
    public TradeCommentResponse execute(Long userId, String tradeId, String commentId) {
        TradePost tradePost = tradePostSupport.findOpenPostForUpdate(tradeId);
        TradeComment tradeComment = tradeCommentRepository.findByCommentId(commentId)
                .orElseThrow(() -> TradeCommentNotFoundException.EXCEPTION);

        if (!tradeComment.getTradePost().getId().equals(tradePost.getId())) {
            throw TradeCommentNotFoundException.EXCEPTION;
        }

        if (!tradeComment.isWrittenBy(userId)) {
            throw TradeForbiddenException.EXCEPTION;
        }

        TradeCommentResponse response = toResponse(tradeComment);
        Map<Item, Integer> items = tradeCommentSupport.getCommentItems(tradeComment);
        tradeTransactionService.releaseItems(tradeComment.getAuthor(), items);
        tradeCommentItemRepository.deleteAllByTradeCommentId(tradeComment.getId());
        tradeCommentRepository.delete(tradeComment);

        return response;
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
}
