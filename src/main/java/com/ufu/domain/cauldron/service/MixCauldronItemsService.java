package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.exception.CauldronMixInsufficientItemQuantityException;
import com.ufu.domain.cauldron.exception.CauldronMixResultItemNotAvailableException;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronMixResponse;
import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.exception.ItemNotFoundException;
import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import com.ufu.domain.item.repository.ItemRepository;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MixCauldronItemsService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final CauldronUserItemSupport cauldronUserItemSupport;

    @Transactional
    public CauldronMixResponse execute(Long userId, List<String> materialItemIds) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        Map<String, Integer> materialQuantities = getMixMaterialQuantities(materialItemIds);
        validateMixMaterialItems(materialQuantities);
        List<UserItem> materialUserItems = getMixMaterialUserItems(userId, materialQuantities);

        validateMixMaterialQuantities(materialUserItems, materialQuantities);
        Item resultItem = getMixResultItem(materialQuantities.keySet().stream().toList());
        materialUserItems.forEach(userItem -> userItem.decreaseQuantity(
                materialQuantities.get(userItem.getItem().getItemId())
        ));

        UserItem resultUserItem = cauldronUserItemSupport.addResultItem(user, resultItem);
        List<MyItemSummaryResponse> changedItems = Stream.concat(
                        materialUserItems.stream(),
                        Stream.of(resultUserItem)
                )
                .map(MyItemSummaryResponse::new)
                .toList();

        materialUserItems.stream()
                .filter(UserItem::isEmpty)
                .forEach(userItemRepository::delete);

        return new CauldronMixResponse(
                new MyItemSummaryResponse(resultUserItem),
                changedItems
        );
    }

    private Map<String, Integer> getMixMaterialQuantities(List<String> materialItemIds) {
        return materialItemIds.stream()
                .collect(Collectors.toMap(
                        itemId -> itemId,
                        itemId -> 1,
                        Integer::sum,
                        LinkedHashMap::new
                ));
    }

    private void validateMixMaterialItems(Map<String, Integer> materialQuantities) {
        if (itemRepository.findAllByItemIdIn(materialQuantities.keySet().stream().toList()).size()
                != materialQuantities.size()) {
            throw ItemNotFoundException.EXCEPTION;
        }
    }

    private List<UserItem> getMixMaterialUserItems(Long userId, Map<String, Integer> materialQuantities) {
        return materialQuantities.keySet().stream()
                .map(itemId -> userItemRepository.findByUserIdAndItemItemIdForUpdate(userId, itemId)
                        .orElseThrow(() -> CauldronMixInsufficientItemQuantityException.EXCEPTION))
                .toList();
    }

    private void validateMixMaterialQuantities(
            List<UserItem> materialUserItems,
            Map<String, Integer> materialQuantities
    ) {
        boolean hasInsufficientQuantity = materialUserItems.stream()
                .anyMatch(userItem -> !userItem.hasAvailableQuantity(
                        materialQuantities.get(userItem.getItem().getItemId())
                ));

        if (hasInsufficientQuantity) {
            throw CauldronMixInsufficientItemQuantityException.EXCEPTION;
        }
    }

    private Item getMixResultItem(List<String> materialItemIds) {
        List<Item> candidates = itemRepository.findAllByStatusAndItemIdNotIn(ItemStatus.GACHA, materialItemIds);

        if (candidates.isEmpty()) {
            throw CauldronMixResultItemNotAvailableException.EXCEPTION;
        }

        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }
}
