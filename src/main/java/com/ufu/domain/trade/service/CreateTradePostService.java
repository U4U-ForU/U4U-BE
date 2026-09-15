package com.ufu.domain.trade.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.domain.TradePostItem;
import com.ufu.domain.trade.presentation.dto.request.TradePostCreateRequest;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.presentation.dto.response.TradePostDetailResponse;
import com.ufu.domain.trade.repository.TradePostItemRepository;
import com.ufu.domain.trade.repository.TradePostRepository;
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
public class CreateTradePostService {
    private final TradePostRepository tradePostRepository;
    private final TradePostItemRepository tradePostItemRepository;
    private final UserRepository userRepository;
    private final TradeTransactionService tradeTransactionService;

    @Transactional
    public TradePostDetailResponse execute(Long userId, TradePostCreateRequest request) {
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

    private TradeItemResponse saveTradePostItem(TradePost tradePost, Item item, int quantity) {
        TradePostItem tradePostItem = TradePostItem.builder()
                .tradePost(tradePost)
                .item(item)
                .quantity(quantity)
                .build();

        tradePostItemRepository.save(tradePostItem);
        return new TradeItemResponse(item, quantity);
    }
}
