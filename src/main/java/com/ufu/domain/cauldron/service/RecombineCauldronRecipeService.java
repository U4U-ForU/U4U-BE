package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.exception.CauldronRecombineInsufficientItemQuantityException;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecombineResponse;
import com.ufu.domain.cauldron.repository.CauldronRecipeMaterialRepository;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecombineCauldronRecipeService {
    private final UserRepository userRepository;
    private final CauldronRecipeMaterialRepository cauldronRecipeMaterialRepository;
    private final UserItemRepository userItemRepository;
    private final CauldronRecipeSupport cauldronRecipeSupport;
    private final CauldronUserItemSupport cauldronUserItemSupport;

    @Transactional
    public CauldronRecombineResponse execute(Long userId, String recipeId) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        CauldronRecipe recipe = cauldronRecipeSupport.findActiveRecipe(recipeId);
        Map<String, Integer> materialQuantities = getMaterialQuantities(recipe);
        List<UserItem> materialUserItems = getMaterialUserItems(userId, materialQuantities);

        validateMaterialQuantities(materialUserItems, materialQuantities);
        materialUserItems.forEach(userItem -> userItem.decreaseQuantity(
                materialQuantities.get(userItem.getItem().getItemId())
        ));

        UserItem resultUserItem = cauldronUserItemSupport.addResultItem(user, recipe.getResultItem());
        List<MyItemSummaryResponse> changedItems = Stream.concat(
                materialUserItems.stream(),
                Stream.of(resultUserItem)
        )
                .map(MyItemSummaryResponse::new)
                .toList();

        materialUserItems.stream()
                .filter(UserItem::isEmpty)
                .forEach(userItemRepository::delete);

        return new CauldronRecombineResponse(
                new MyItemSummaryResponse(resultUserItem),
                changedItems
        );
    }

    private Map<String, Integer> getMaterialQuantities(CauldronRecipe recipe) {
        return cauldronRecipeMaterialRepository
                .findAllWithItemByCauldronRecipeIdOrderBySlotNumberAsc(recipe.getId())
                .stream()
                .collect(Collectors.toMap(
                        material -> material.getItem().getItemId(),
                        material -> 1,
                        Integer::sum,
                        java.util.LinkedHashMap::new
                ));
    }

    private List<UserItem> getMaterialUserItems(Long userId, Map<String, Integer> materialQuantities) {
        return materialQuantities.keySet().stream()
                .map(itemId -> userItemRepository.findByUserIdAndItemItemIdForUpdate(userId, itemId)
                        .orElseThrow(() -> CauldronRecombineInsufficientItemQuantityException.EXCEPTION))
                .toList();
    }

    private void validateMaterialQuantities(
            List<UserItem> materialUserItems,
            Map<String, Integer> materialQuantities
    ) {
        boolean hasInsufficientQuantity = materialUserItems.stream()
                .anyMatch(userItem -> !userItem.hasAvailableQuantity(
                        materialQuantities.get(userItem.getItem().getItemId())
                ));

        if (hasInsufficientQuantity) {
            throw CauldronRecombineInsufficientItemQuantityException.EXCEPTION;
        }
    }
}
