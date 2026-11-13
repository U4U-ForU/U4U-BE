package com.ufu.domain.item.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.presentation.dto.request.AdminItemSortType;
import com.ufu.domain.item.presentation.dto.response.AdminItemListResponse;
import com.ufu.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAdminItemListService {
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<AdminItemListResponse> execute(ItemStatus status, AdminItemSortType sortType, String keyword) {
        String searchKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;

        return itemRepository.findAllWithCreatorByStatusAndKeyword(status, searchKeyword)
                .stream()
                .sorted(getComparator(sortType))
                .map(AdminItemListResponse::new)
                .toList();
    }

    private Comparator<Item> getComparator(AdminItemSortType sortType) {
        return switch (sortType) {
            case APPROVED_AT_DESC -> Comparator.comparing(Item::getApprovedAt).reversed();
            case APPROVED_AT_ASC -> Comparator.comparing(Item::getApprovedAt);
            case NAME_ASC -> Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER);
        };
    }
}
