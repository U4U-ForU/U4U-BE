package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeMaterial;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import com.ufu.domain.cauldron.exception.CauldronRecipeNotFoundException;
import com.ufu.domain.cauldron.exception.CauldronRecipeResultItemInvalidException;
import com.ufu.domain.cauldron.presentation.dto.request.CauldronRecipeRequest;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeDeleteResponse;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.repository.CauldronRecipeMaterialRepository;
import com.ufu.domain.cauldron.repository.CauldronRecipeRepository;
import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.exception.ItemNotFoundException;
import com.ufu.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CauldronRecipeService {
    private static final int MATERIAL_COUNT = 3;

    private final CauldronRecipeRepository cauldronRecipeRepository;
    private final CauldronRecipeMaterialRepository cauldronRecipeMaterialRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public CauldronRecipeResponse create(CauldronRecipeRequest request) {
        List<Item> materials = getMaterialItems(request.getMaterialItemIds());
        Item resultItem = getCombinationResultItem(request.getResultItemId());

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
    public CauldronRecipeResponse update(String recipeId, CauldronRecipeRequest request) {
        CauldronRecipe recipe = findActiveRecipe(recipeId);
        List<Item> materials = getMaterialItems(request.getMaterialItemIds());
        Item resultItem = getCombinationResultItem(request.getResultItemId());

        recipe.changeResultItem(resultItem);
        changeMaterials(recipe, materials);

        return toResponse(recipe);
    }

    @Transactional
    public CauldronRecipeDeleteResponse delete(String recipeId) {
        CauldronRecipe recipe = findActiveRecipe(recipeId);
        recipe.delete(LocalDateTime.now());
        return new CauldronRecipeDeleteResponse(recipe);
    }

    private CauldronRecipe findActiveRecipe(String recipeId) {
        return cauldronRecipeRepository.findWithResultItemByRecipeIdAndStatus(recipeId, CauldronRecipeStatus.ACTIVE)
                .orElseThrow(() -> CauldronRecipeNotFoundException.EXCEPTION);
    }

    private Item getCombinationResultItem(String resultItemId) {
        Item resultItem = itemRepository.findByItemId(resultItemId)
                .orElseThrow(() -> ItemNotFoundException.EXCEPTION);

        if (resultItem.getStatus() != ItemStatus.COMBINATION) {
            throw CauldronRecipeResultItemInvalidException.EXCEPTION;
        }

        return resultItem;
    }

    private List<Item> getMaterialItems(List<String> materialItemIds) {
        return materialItemIds.stream()
                .map(itemId -> itemRepository.findByItemId(itemId)
                        .orElseThrow(() -> ItemNotFoundException.EXCEPTION))
                .toList();
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
