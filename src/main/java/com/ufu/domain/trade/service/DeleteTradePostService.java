package com.ufu.domain.trade.service;

import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.presentation.dto.response.TradePostDeleteResponse;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DeleteTradePostService {
    private final TradeCommentRepository tradeCommentRepository;
    private final TradeTransactionService tradeTransactionService;
    private final TradePostSupport tradePostSupport;
    private final TradeCommentSupport tradeCommentSupport;

    @Transactional
    public TradePostDeleteResponse execute(Long userId, String tradeId) {
        TradePost tradePost = tradePostSupport.findOpenPostForUpdate(tradeId);
        tradePostSupport.verifyAuthor(tradePost, userId);
        tradeTransactionService.lockUsers(Stream.concat(
                        Stream.of(tradePost.getAuthor().getId()),
                        tradeCommentRepository
                                .findAllByTradePostIdAndStatusOrderByCreatedAtAsc(
                                        tradePost.getId(),
                                        TradeCommentStatus.PENDING
                                )
                                .stream()
                                .map(comment -> comment.getAuthor().getId())
                )
                .toList());
        tradeTransactionService.releaseItems(tradePost.getAuthor(), tradePostSupport.getPostItems(tradePost));
        tradeCommentSupport.deletePendingComments(tradePost, null);
        tradePost.delete();

        return new TradePostDeleteResponse(tradePost.getTradeId(), tradePost.getDeletedAt());
    }
}
