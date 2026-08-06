package com.ufu.domain.trade.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.repository.ItemRepository;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.trade.exception.TradeInsufficientItemQuantityException;
import com.ufu.domain.trade.exception.TradeItemQuantityException;
import com.ufu.domain.trade.presentation.dto.request.TradeItemRequest;
import com.ufu.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeTransactionService {
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;

    public Map<Item, Integer> reserveItems(Long userId, List<TradeItemRequest> requests) {
        Map<String, Integer> quantities = aggregateQuantities(requests);
        Map<Item, Integer> items = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
            Item item = itemRepository.findByItemId(entry.getKey())
                    .orElseThrow(() -> TradeInsufficientItemQuantityException.EXCEPTION);
            UserItem userItem = userItemRepository.findByUserIdAndItemItemIdForUpdate(userId, entry.getKey())
                    .orElseThrow(() -> TradeInsufficientItemQuantityException.EXCEPTION);

            if (!userItem.hasAvailableQuantity(entry.getValue())) {
                throw TradeInsufficientItemQuantityException.EXCEPTION;
            }

            userItem.reserveQuantity(entry.getValue());
            items.put(item, entry.getValue());
        }
        return items;

    }

    public void releaseItems(User user, Map<Item, Integer> items) {
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            UserItem userItem = userItemRepository.findByUserIdAndItemItemIdForUpdate(user.getId(), entry.getKey().getItemId())
                    .orElseThrow(() -> TradeInsufficientItemQuantityException.EXCEPTION);
            userItem.releaseReservedQuantity(entry.getValue());
        }
    }

    public void transferItems(User from, User to, Map<Item, Integer> items) {
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();
            UserItem outgoing = userItemRepository.findByUserIdAndItemIdForUpdate(from.getId(), item.getId())
                    .orElseThrow(() -> TradeInsufficientItemQuantityException.EXCEPTION);

            outgoing.transferReservedQuantity(quantity);

            Optional<UserItem> incoming = userItemRepository.findByUserIdAndItemIdForUpdate(to.getId(), item.getId());
            if (incoming.isPresent()) {
                incoming.get().increaseQuantity(quantity);
                continue;
            }

            userItemRepository.save(UserItem.builder()
                    .user(to)
                    .item(item)
                    .quantity(quantity)
                    .build());
        }
    }

    private Map<String, Integer> aggregateQuantities(List<TradeItemRequest> requests) {
        Map<String, Integer> quantities = new LinkedHashMap<>();

        for (TradeItemRequest request : requests) {
            quantities.merge(request.getItemId(), request.getQuantity(), Integer::sum);
        }

        int totalQuantity = quantities.values().stream().mapToInt(Integer::intValue).sum();
        if (totalQuantity < 1 || totalQuantity > 10) {
            throw TradeItemQuantityException.EXCEPTION;
        }

        return quantities;
    }
}
