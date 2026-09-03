package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeMaterial;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import com.ufu.domain.cauldron.exception.CauldronMixInsufficientItemQuantityException;
import com.ufu.domain.cauldron.exception.CauldronMixResultItemNotAvailableException;
import com.ufu.domain.cauldron.exception.CauldronRecipeNotFoundException;
import com.ufu.domain.cauldron.exception.CauldronRecombineInsufficientItemQuantityException;
import com.ufu.domain.cauldron.exception.CauldronRecipeResultItemAlreadyExistsException;
import com.ufu.domain.cauldron.exception.CauldronRecipeResultItemChangeForbiddenException;
import com.ufu.domain.cauldron.exception.CauldronRecipeResultItemInMaterialsException;
import com.ufu.domain.cauldron.exception.CauldronRecipeResultItemInvalidException;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronMixResponse;
import com.ufu.domain.cauldron.presentation.dto.request.CauldronRecipeRequest;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeDeleteResponse;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecombineResponse;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.repository.CauldronRecipeMaterialRepository;
import com.ufu.domain.cauldron.repository.CauldronRecipeRepository;
import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.exception.ItemNotFoundException;
import com.ufu.domain.item.repository.ItemRepository;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CauldronRecipeService {
    private static final int MATERIAL_COUNT = 3;

    private final CauldronRecipeRepository cauldronRecipeRepository;
    private final CauldronRecipeMaterialRepository cauldronRecipeMaterialRepository;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public CauldronRecipeResponse create(CauldronRecipeRequest request) {
        Item resultItem = getLockedCombinationResultItem(request.getResultItemId());
        validateResultItemIsNotMaterial(request);
        List<Item> materials = getMaterialItems(request.getMaterialItemIds());
        validateNoActiveRecipeForResultItem(resultItem);

        CauldronRecipe recipe = cauldronRecipeRepository.save(CauldronRecipe.builder()
                .resultItem(resultItem)
                .build());
        saveMaterials(recipe, materials);

        return toResponse(recipe);
    }

    @Transactional(readOnly = true)
    public List<CauldronRecipeResponse> getAll() {
        List<CauldronRecipe> recipes = cauldronRecipeRepository
                .findAllWithResultItemByStatus(CauldronRecipeStatus.ACTIVE);

        if (recipes.isEmpty()) {
            return List.of();
        }

        List<Long> recipeIds = recipes.stream()
                .map(CauldronRecipe::getId)
                .toList();
        Map<Long, List<CauldronRecipeMaterial>> materialsByRecipeId = cauldronRecipeMaterialRepository
                .findAllWithItemByCauldronRecipeIdInOrderByCauldronRecipeIdAscSlotNumberAsc(recipeIds)
                .stream()
                .collect(Collectors.groupingBy(material -> material.getCauldronRecipe().getId()));

        return recipes.stream()
                .map(recipe -> new CauldronRecipeResponse(
                        recipe,
                        materialsByRecipeId.getOrDefault(recipe.getId(), List.of())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public CauldronRecipeResponse get(String recipeId) {
        return toResponse(findActiveRecipe(recipeId));
    }

    @Transactional
    public CauldronRecombineResponse recombine(Long userId, String recipeId) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        CauldronRecipe recipe = findActiveRecipe(recipeId);
        Map<String, Integer> materialQuantities = getMaterialQuantities(recipe);
        List<UserItem> materialUserItems = getMaterialUserItems(userId, materialQuantities);

        validateMaterialQuantities(materialUserItems, materialQuantities);
        materialUserItems.forEach(userItem -> userItem.decreaseQuantity(
                materialQuantities.get(userItem.getItem().getItemId())
        ));

        UserItem resultUserItem = addResultItem(user, recipe.getResultItem());
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

    @Transactional
    public CauldronMixResponse mix(Long userId, List<String> materialItemIds) {
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

        UserItem resultUserItem = addResultItem(user, resultItem);
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

    @Transactional
    public CauldronRecipeResponse update(String recipeId, CauldronRecipeRequest request) {
        CauldronRecipe recipe = findActiveRecipeForUpdate(recipeId);
        validateResultItemIsUnchanged(recipe, request);
        getLockedCombinationResultItem(recipe.getResultItem().getItemId());
        validateResultItemIsNotMaterial(request);
        List<Item> materials = getMaterialItems(request.getMaterialItemIds());

        changeMaterials(recipe, materials);

        return toResponse(recipe);
    }

    @Transactional
    public CauldronRecipeDeleteResponse delete(String recipeId) {
        CauldronRecipe recipe = findActiveRecipeForUpdate(recipeId);
        lockItems(recipe.getResultItem().getItemId());
        recipe.delete(LocalDateTime.now());
        return new CauldronRecipeDeleteResponse(recipe);
    }

    private CauldronRecipe findActiveRecipe(String recipeId) {
        return cauldronRecipeRepository.findWithResultItemByRecipeIdAndStatus(recipeId, CauldronRecipeStatus.ACTIVE)
                .orElseThrow(() -> CauldronRecipeNotFoundException.EXCEPTION);
    }

    private CauldronRecipe findActiveRecipeForUpdate(String recipeId) {
        return cauldronRecipeRepository
                .findWithResultItemByRecipeIdAndStatusForUpdate(recipeId, CauldronRecipeStatus.ACTIVE)
                .orElseThrow(() -> CauldronRecipeNotFoundException.EXCEPTION);
    }

    private Item getLockedCombinationResultItem(String... itemIds) {
        Map<String, Item> itemsByItemId = lockItems(itemIds).stream()
                .collect(Collectors.toMap(Item::getItemId, item -> item));
        String resultItemId = itemIds[itemIds.length - 1];
        Item resultItem = itemsByItemId.get(resultItemId);

        if (resultItem == null) {
            throw ItemNotFoundException.EXCEPTION;
        }

        if (resultItem.getStatus() != ItemStatus.COMBINATION) {
            throw CauldronRecipeResultItemInvalidException.EXCEPTION;
        }

        return resultItem;
    }

    private List<Item> lockItems(String... itemIds) {
        return itemRepository.findAllByItemIdInForUpdateOrderByIdAsc(
                Arrays.stream(itemIds)
                        .distinct()
                        .toList()
        );
    }

    private void validateResultItemIsNotMaterial(CauldronRecipeRequest request) {
        if (request.getMaterialItemIds().contains(request.getResultItemId())) {
            throw CauldronRecipeResultItemInMaterialsException.EXCEPTION;
        }
    }

    private void validateResultItemIsUnchanged(CauldronRecipe recipe, CauldronRecipeRequest request) {
        if (!recipe.getResultItem().getItemId().equals(request.getResultItemId())) {
            throw CauldronRecipeResultItemChangeForbiddenException.EXCEPTION;
        }
    }

    private void validateNoActiveRecipeForResultItem(Item resultItem) {
        if (cauldronRecipeRepository.existsByResultItemAndStatus(resultItem, CauldronRecipeStatus.ACTIVE)) {
            throw CauldronRecipeResultItemAlreadyExistsException.EXCEPTION;
        }
    }

    private List<Item> getMaterialItems(List<String> materialItemIds) {
        return materialItemIds.stream()
                .map(itemId -> itemRepository.findByItemId(itemId)
                        .orElseThrow(() -> ItemNotFoundException.EXCEPTION))
                .toList();
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

    private UserItem addResultItem(User user, Item resultItem) {
        return userItemRepository.findByUserIdAndItemIdForUpdate(user.getId(), resultItem.getId())
                .map(userItem -> {
                    userItem.increaseQuantity(1);
                    return userItem;
                })
                .orElseGet(() -> userItemRepository.save(UserItem.builder()
                        .user(user)
                        .item(resultItem)
                        .quantity(1)
                        .build()));
    }

    private void saveMaterials(CauldronRecipe recipe, List<Item> materials) {
        List<CauldronRecipeMaterial> recipeMaterials = IntStream.range(0, MATERIAL_COUNT)
                .mapToObj(index -> CauldronRecipeMaterial.builder()
                        .cauldronRecipe(recipe)
                        .item(materials.get(index))
                        .slotNumber(index + 1)
                        .build())
                .toList();

        cauldronRecipeMaterialRepository.saveAll(recipeMaterials);
    }

    private void changeMaterials(CauldronRecipe recipe, List<Item> items) {
        List<CauldronRecipeMaterial> materials = cauldronRecipeMaterialRepository
                .findAllWithItemByCauldronRecipeIdOrderBySlotNumberAsc(recipe.getId());

        IntStream.range(0, MATERIAL_COUNT)
                .forEach(index -> materials.get(index).changeItem(items.get(index)));
    }

    private CauldronRecipeResponse toResponse(CauldronRecipe recipe) {
        List<CauldronRecipeMaterial> materials = cauldronRecipeMaterialRepository
                .findAllWithItemByCauldronRecipeIdOrderBySlotNumberAsc(recipe.getId());
        return new CauldronRecipeResponse(recipe, materials);
    }
}
