package com.ufu.domain.item.service;

import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.exception.InventoryItemNotFoundException;
import com.ufu.domain.item.presentation.dto.response.MyItemDetailResponse;
import com.ufu.domain.item.repository.UserItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyItemDetailService {
    private final UserItemRepository userItemRepository;

    @Transactional(readOnly = true)
    public MyItemDetailResponse execute(Long userId, String itemId) {
        UserItem userItem = userItemRepository.findByUserIdAndItemItemId(userId, itemId)
                .orElseThrow(() -> InventoryItemNotFoundException.EXCEPTION);

        return new MyItemDetailResponse(userItem);
    }
}
