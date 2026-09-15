package com.ufu.domain.trade.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentItem;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.presentation.dto.response.TradeCommentResponse;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.repository.TradeCommentItemRepository;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 거래 제안 조회/삭제를 여러 유스케이스가 공유하기 위한 헬퍼다.
 *
 * <p><b>이 클래스에는 절대 {@code @Transactional}을 붙이지 않는다.</b>
 * 호출자의 트랜잭션 안에서 실행되어야 하며, 자체 트랜잭션을 열면 아래가 모두 깨진다.
 * <ul>
 *   <li>{@code findByTradeIdForUpdate}가 거는 비관적 쓰기 락이 즉시 풀린다.</li>
 *   <li>{@code findByTradeIdForUpdate}는 {@code join fetch}가 없어 작성자를 함께 가져오지
 *       않는다. {@code toDetailResponse}의 {@code getAuthor().getLoginId()}는 호출자의
 *       트랜잭션이 열려 있어 지연 로딩이 되기 때문에 동작한다.</li>
 *   <li>{@code Item}에 {@code equals}/{@code hashCode}가 없어 {@code Map<Item, Integer>}가
 *       객체 동일성으로 키를 잡는다. 하나의 영속성 컨텍스트 안에서만 안전하다.</li>
 * </ul>
 * 같은 이유로 Controller에 직접 주입하지 않는다.
 * ({@code TradeTransactionService}와 동일한 규약이다.)
 */
@Service
@RequiredArgsConstructor
public class TradeCommentSupport {
    private final TradeCommentRepository tradeCommentRepository;
    private final TradeCommentItemRepository tradeCommentItemRepository;
    private final TradeTransactionService tradeTransactionService;

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

    public Map<Long, List<TradeItemResponse>> getItemResponsesByCommentId(List<Long> tradeCommentIds) {
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

    public Map<Item, Integer> getCommentItems(TradeComment tradeComment) {
        return tradeCommentItemRepository.findAllWithItemByTradeCommentId(tradeComment.getId())
                .stream()
                .collect(Collectors.toMap(
                        TradeCommentItem::getItem,
                        TradeCommentItem::getQuantity,
                        Integer::sum,
                        LinkedHashMap::new
                ));
    }

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
}
