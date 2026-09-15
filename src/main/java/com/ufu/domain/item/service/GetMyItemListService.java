package com.ufu.domain.item.service;

import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.presentation.dto.request.ItemSortType;
import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import com.ufu.domain.item.repository.UserItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyItemListService {
    private final UserItemRepository userItemRepository;

    @Transactional(readOnly = true)
    public List<MyItemSummaryResponse> execute(Long userId, ItemSortType sortType, String keyword) {
        String searchKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;

        return userItemRepository.findAllByUserIdAndKeyword(userId, searchKeyword)
                .stream()
                .sorted(getComparator(sortType))
                .map(MyItemSummaryResponse::new)
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
}
