package com.ufu.domain.trade.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.domain.TradePostItem;
import com.ufu.domain.trade.exception.TradeForbiddenException;
import com.ufu.domain.trade.exception.TradePostCompletedException;
import com.ufu.domain.trade.exception.TradePostInvalidStateException;
import com.ufu.domain.trade.exception.TradePostNotFoundException;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.presentation.dto.response.TradePostDetailResponse;
import com.ufu.domain.trade.repository.TradePostItemRepository;
import com.ufu.domain.trade.repository.TradePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 거래 게시물 조회/검증/응답 변환을 여러 유스케이스가 공유하기 위한 헬퍼다.
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
public class TradePostSupport {
    private final TradePostRepository tradePostRepository;
    private final TradePostItemRepository tradePostItemRepository;
    private final TradeCommentSupport tradeCommentSupport;

    public TradePost findOpenPost(String tradeId) {
        TradePost tradePost = tradePostRepository.findByTradeId(tradeId)
                .orElseThrow(() -> TradePostNotFoundException.EXCEPTION);

        validateOpen(tradePost);
        return tradePost;
    }

    public TradePost findOpenPostForUpdate(String tradeId) {
        TradePost tradePost = tradePostRepository.findByTradeIdForUpdate(tradeId)
                .orElseThrow(() -> TradePostNotFoundException.EXCEPTION);

        validateOpen(tradePost);
        return tradePost;
    }

    public void validateOpen(TradePost tradePost) {
        if (tradePost.isCompleted()) {
            throw TradePostCompletedException.EXCEPTION;
        }

        if (!tradePost.isOpen()) {
            throw TradePostInvalidStateException.EXCEPTION;
        }
    }

    public void verifyAuthor(TradePost tradePost, Long userId) {
        if (!tradePost.isWrittenBy(userId)) {
            throw TradeForbiddenException.EXCEPTION;
        }
    }

    public Map<Item, Integer> getPostItems(TradePost tradePost) {
        return tradePostItemRepository.findAllWithItemByTradePostId(tradePost.getId())
                .stream()
                .collect(Collectors.toMap(
                        TradePostItem::getItem,
                        TradePostItem::getQuantity,
                        Integer::sum,
                        LinkedHashMap::new
                ));
    }

    public List<TradeItemResponse> getPostItemResponses(TradePost tradePost) {
        return tradePostItemRepository.findAllWithItemByTradePostId(tradePost.getId())
                .stream()
                .map(item -> new TradeItemResponse(item.getItem(), item.getQuantity()))
                .toList();
    }

    public TradePostDetailResponse toDetailResponse(TradePost tradePost) {
        return new TradePostDetailResponse(
                tradePost.getTradeId(),
                tradePost.getTitle(),
                tradePost.getAuthor().getLoginId(),
                getPostItemResponses(tradePost),
                tradeCommentSupport.getPendingComments(tradePost.getId()),
                tradePost.getCreatedAt()
        );
    }
}
