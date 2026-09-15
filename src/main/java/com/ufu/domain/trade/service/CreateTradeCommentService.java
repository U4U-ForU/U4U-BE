package com.ufu.domain.trade.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentItem;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.exception.TradeCommentAlreadyExistsException;
import com.ufu.domain.trade.exception.TradeForbiddenException;
import com.ufu.domain.trade.presentation.dto.request.TradeCommentCreateRequest;
import com.ufu.domain.trade.presentation.dto.response.TradeCommentResponse;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.repository.TradeCommentItemRepository;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateTradeCommentService {
    private final TradeCommentRepository tradeCommentRepository;
    private final TradeCommentItemRepository tradeCommentItemRepository;
    private final UserRepository userRepository;
    private final TradeTransactionService tradeTransactionService;
    private final TradePostSupport tradePostSupport;

    @Transactional
    public TradeCommentResponse execute(
            Long userId,
            String tradeId,
            TradeCommentCreateRequest request
    ) {
        TradePost tradePost = tradePostSupport.findOpenPostForUpdate(tradeId);
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

    private TradeItemResponse saveTradeCommentItem(TradeComment tradeComment, Item item, int quantity) {
        tradeCommentItemRepository.save(TradeCommentItem.builder()
                .tradeComment(tradeComment)
                .item(item)
                .quantity(quantity)
                .build());

        return new TradeItemResponse(item, quantity);
    }
}
