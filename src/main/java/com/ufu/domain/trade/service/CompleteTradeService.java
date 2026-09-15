package com.ufu.domain.trade.service;

import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.exception.TradeCommentNotFoundException;
import com.ufu.domain.trade.exception.TradeForbiddenException;
import com.ufu.domain.trade.presentation.dto.response.TradeCompletionResponse;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CompleteTradeService {
    private static final int TRADE_REWARD = 1;

    private final TradeCommentRepository tradeCommentRepository;
    private final TradeTransactionService tradeTransactionService;
    private final TradePostSupport tradePostSupport;
    private final TradeCommentSupport tradeCommentSupport;

    @Transactional
    public TradeCompletionResponse execute(Long userId, String tradeId, String commentId) {
        TradePost tradePost = tradePostSupport.findOpenPostForUpdate(tradeId);
        if (!tradePost.isWrittenBy(userId)) {
            throw TradeForbiddenException.EXCEPTION;
        }

        TradeComment acceptedComment = tradeCommentRepository.findByCommentId(commentId)
                .orElseThrow(() -> TradeCommentNotFoundException.EXCEPTION);

        if (!acceptedComment.getTradePost().getId().equals(tradePost.getId()) || !acceptedComment.isPending()) {
            throw TradeCommentNotFoundException.EXCEPTION;
        }

        List<TradeComment> pendingComments = tradeCommentRepository
                .findAllByTradePostIdAndStatusOrderByCreatedAtAsc(tradePost.getId(), TradeCommentStatus.PENDING);
        tradeTransactionService.lockUsers(Stream.concat(
                        Stream.of(tradePost.getAuthor().getId()),
                        pendingComments.stream().map(comment -> comment.getAuthor().getId())
                )
                .toList());

        tradeTransactionService.transferItems(
                tradePost.getAuthor(),
                acceptedComment.getAuthor(),
                tradePostSupport.getPostItems(tradePost)
        );
        tradeTransactionService.transferItems(
                acceptedComment.getAuthor(),
                tradePost.getAuthor(),
                tradeCommentSupport.getCommentItems(acceptedComment)
        );
        tradePost.getAuthor().increaseCurrency(TRADE_REWARD);
        acceptedComment.getAuthor().increaseCurrency(TRADE_REWARD);

        acceptedComment.accept();
        tradeCommentSupport.deletePendingComments(tradePost, acceptedComment.getCommentId());
        tradePost.complete();

        return new TradeCompletionResponse(
                tradePost.getTradeId(),
                acceptedComment.getCommentId(),
                tradePost.getCompletedAt()
        );
    }
}
