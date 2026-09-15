package com.ufu.domain.item.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.presentation.dto.response.AdminItemSummaryResponse;
import com.ufu.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAdminItemListService {
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<AdminItemSummaryResponse> execute(ItemStatus status) {
        List<Item> items = status == null
                ? itemRepository.findAllByOrderByApprovedAtDesc()
                : itemRepository.findAllByStatusOrderByApprovedAtDesc(status);

        return items.stream()
                .map(AdminItemSummaryResponse::new)
                .toList();
    }
}
