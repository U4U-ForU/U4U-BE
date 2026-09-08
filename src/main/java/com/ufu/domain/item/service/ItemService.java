package com.ufu.domain.item.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.exception.InventoryItemNotFoundException;
import com.ufu.domain.item.presentation.dto.request.ItemSortType;
import com.ufu.domain.item.presentation.dto.response.MyItemDetailResponse;
import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import com.ufu.domain.item.presentation.dto.response.MyTradingItemGroupResponse;
import com.ufu.domain.item.presentation.dto.response.AdminItemSummaryResponse;
import com.ufu.domain.item.repository.ItemRepository;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.trade.domain.TradeComment;
import com.ufu.domain.trade.domain.TradeCommentStatus;
import com.ufu.domain.trade.domain.TradePost;
import com.ufu.domain.trade.domain.TradePostStatus;
import com.ufu.domain.trade.presentation.dto.response.TradeItemResponse;
import com.ufu.domain.trade.repository.TradeCommentItemRepository;
import com.ufu.domain.trade.repository.TradeCommentRepository;
import com.ufu.domain.trade.repository.TradePostItemRepository;
import com.ufu.domain.trade.repository.TradePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final TradePostRepository tradePostRepository;
    private final TradePostItemRepository tradePostItemRepository;
    private final TradeCommentRepository tradeCommentRepository;
    private final TradeCommentItemRepository tradeCommentItemRepository;

    @Transactional(readOnly = true)
    public List<MyItemSummaryResponse> getMyItems(Long userId, ItemSortType sortType, String keyword) {
        String searchKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;

        return userItemRepository.findAllByUserIdAndKeyword(userId, searchKeyword)
                .stream()
                .sorted(getComparator(sortType))
                .map(MyItemSummaryResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public MyItemDetailResponse getMyItemDetail(Long userId, String itemId) {
        UserItem userItem = userItemRepository.findByUserIdAndItemItemId(userId, itemId)
                .orElseThrow(() -> InventoryItemNotFoundException.EXCEPTION);

        return new MyItemDetailResponse(userItem);
    }

    @Transactional(readOnly = true)
    public List<AdminItemSummaryResponse> getAdminItems(ItemStatus status) {
        List<Item> items = status == null
                ? itemRepository.findAllByOrderByApprovedAtDesc()
                : itemRepository.findAllByStatusOrderByApprovedAtDesc(status);

        return items.stream()
                .map(AdminItemSummaryResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyTradingItemGroupResponse> getMyTradingItems(Long userId) {
        List<TradePost> tradePosts = tradePostRepository
                .findAllByAuthorIdAndStatusOrderByCreatedAtDesc(userId, TradePostStatus.OPEN);
        List<TradeComment> tradeComments = tradeCommentRepository
                .findAllWithTradePostByAuthorIdAndStatusOrderByCreatedAtDesc(userId, TradeCommentStatus.PENDING)
                .stream()
                .filter(comment -> comment.getTradePost().isOpen())
                .toList();

        Map<Long, List<TradeItemResponse>> itemsByPostId = getItemResponsesByPostId(tradePosts);
        Map<Long, List<TradeItemResponse>> itemsByCommentId = getItemResponsesByCommentId(tradeComments);

        Stream<MyTradingItemGroupResponse> posts = tradePosts.stream()
                .map(tradePost -> new MyTradingItemGroupResponse(
                        "POST",
                        tradePost.getTradeId(),
                        tradePost.getTitle(),
                        itemsByPostId.getOrDefault(tradePost.getId(), List.of()),
                        tradePost.getCreatedAt()
                ));

        Stream<MyTradingItemGroupResponse> comments = tradeComments.stream()
                .map(tradeComment -> new MyTradingItemGroupResponse(
                        "COMMENT",
                        tradeComment.getTradePost().getTradeId(),
                        tradeComment.getTradePost().getTitle(),
                        itemsByCommentId.getOrDefault(tradeComment.getId(), List.of()),
                        tradeComment.getCreatedAt()
                ));

        return Stream.concat(posts, comments)
                .sorted(Comparator.comparing(MyTradingItemGroupResponse::getCreatedAt).reversed())
                .toList();
    }

    private Comparator<UserItem> getComparator(ItemSortType sortType) {
        Comparator<UserItem> approvedAtDescending = Comparator
                .comparing((UserItem userItem) -> userItem.getItem().getApprovedAt())
                .reversed();

        return switch (sortType) {
            case QUANTITY_DESC -> Comparator.comparingInt(UserItem::getQuantity)
                    .reversed()
                    .thenComparing(approvedAtDescending);
            case QUANTITY_ASC -> Comparator.comparingInt(UserItem::getQuantity)
                    .thenComparing(approvedAtDescending);
            case APPROVED_AT_DESC -> approvedAtDescending;
        };
    }

    private Map<Long, List<TradeItemResponse>> getItemResponsesByPostId(List<TradePost> tradePosts) {
        if (tradePosts.isEmpty()) {
            return Map.of();
        }

        return tradePostItemRepository.findAllWithItemByTradePostIdIn(
                        tradePosts.stream()
                                .map(TradePost::getId)
                                .toList()
                )
                .stream()
                .collect(Collectors.groupingBy(
                        tradePostItem -> tradePostItem.getTradePost().getId(),
                        Collectors.mapping(
                                item -> new TradeItemResponse(item.getItem(), item.getQuantity()),
                                Collectors.toList()
                        )
                ));
    }

    private Map<Long, List<TradeItemResponse>> getItemResponsesByCommentId(List<TradeComment> tradeComments) {
        if (tradeComments.isEmpty()) {
            return Map.of();
        }

        return tradeCommentItemRepository.findAllWithItemByTradeCommentIdIn(
                        tradeComments.stream()
                                .map(TradeComment::getId)
                                .toList()
                )
                .stream()
                .collect(Collectors.groupingBy(
                        tradeCommentItem -> tradeCommentItem.getTradeComment().getId(),
                        Collectors.mapping(
                                item -> new TradeItemResponse(item.getItem(), item.getQuantity()),
                                Collectors.toList()
                        )
                ));
    }
}
