package com.ufu.domain.gacha.service;

import com.ufu.domain.gacha.exception.GachaPoolEmptyException;
import com.ufu.domain.gacha.exception.InsufficientCurrencyException;
import com.ufu.domain.gacha.presentation.dto.response.GachaPullResponse;
import com.ufu.domain.gacha.presentation.dto.response.GachaResultResponse;
import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.repository.ItemRepository;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PullGachaService {
    private static final int COST_PER_PULL = 2;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;

    @Transactional
    public GachaPullResponse execute(Long userId, int count) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        List<Item> gachaPool = itemRepository.findAllByStatusOrderByApprovedAtDesc(ItemStatus.GACHA);

        if (gachaPool.isEmpty()) {
            throw GachaPoolEmptyException.EXCEPTION;
        }

        int totalCost = count * COST_PER_PULL;
        if (!user.hasEnoughCurrency(totalCost)) {
            throw InsufficientCurrencyException.EXCEPTION;
        }

        List<Item> pulledItems = drawItems(gachaPool, count);
        saveInventory(user, pulledItems);
        user.deductCurrency(totalCost);

        return new GachaPullResponse(pulledItems.stream().map(GachaResultResponse::new).toList());
    }

    private List<Item> drawItems(List<Item> gachaPool, int count) {
        return IntStream.range(0, count)
                .mapToObj(ignored -> gachaPool.get(ThreadLocalRandom.current().nextInt(gachaPool.size())))
                .toList();
    }

    private void saveInventory(User user, List<Item> pulledItems) {
        Map<Item, Long> quantitiesByItem = pulledItems.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        quantitiesByItem.forEach((item, quantity) -> userItemRepository.findByUserIdAndItemIdForUpdate(user.getId(), item.getId())
                .ifPresentOrElse(
                        userItem -> userItem.increaseQuantity(quantity.intValue()),
                        () -> userItemRepository.save(UserItem.builder()
                                .user(user)
                                .item(item)
                                .quantity(quantity.intValue())
                                .build())
                ));
    }
}
