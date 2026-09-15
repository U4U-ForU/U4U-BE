package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeMaterial;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import com.ufu.domain.cauldron.exception.CauldronRecipeNotFoundException;
import com.ufu.domain.cauldron.exception.CauldronRecipeResultItemInMaterialsException;
import com.ufu.domain.cauldron.exception.CauldronRecipeResultItemInvalidException;
import com.ufu.domain.cauldron.presentation.dto.request.CauldronRecipeRequest;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.repository.CauldronRecipeMaterialRepository;
import com.ufu.domain.cauldron.repository.CauldronRecipeRepository;
import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.exception.ItemNotFoundException;
import com.ufu.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 조합법 유스케이스들이 공유하는 조회/검증 헬퍼를 모아둔 클래스다.
 *
 * <p><b>이 클래스에는 절대 {@code @Transactional}을 붙이지 않는다.</b>
 * {@code findActiveRecipeForUpdate}와 {@code lockItems}는 비관적 쓰기 락을 걸기 때문에,
 * 호출자의 트랜잭션 안에서 실행되어야 락이 해당 트랜잭션 끝까지 유지된다.
 * 자체 트랜잭션을 열면 락이 즉시 풀리고, 호출자가 방금 저장/수정한 내용이
 * {@code toResponse}에 보이지 않아 응답이 잘못 나간다.
 * 같은 이유로 컨트롤러에 직접 주입해서도 안 된다.
 * ({@code TradeTransactionService}와 동일한 규약이다.)
 */
@Service
@RequiredArgsConstructor
public class CauldronRecipeSupport {
    private final CauldronRecipeRepository cauldronRecipeRepository;
    private final CauldronRecipeMaterialRepository cauldronRecipeMaterialRepository;
    private final CauldronRecipeDependencyValidator cauldronRecipeDependencyValidator;
    private final ItemRepository itemRepository;

    public CauldronRecipe findActiveRecipe(String recipeId) {
        return cauldronRecipeRepository.findWithResultItemByRecipeIdAndStatus(recipeId, CauldronRecipeStatus.ACTIVE)
                .orElseThrow(() -> CauldronRecipeNotFoundException.EXCEPTION);
    }

    public CauldronRecipe findActiveRecipeForUpdate(String recipeId) {
        return cauldronRecipeRepository
                .findWithResultItemByRecipeIdAndStatusForUpdate(recipeId, CauldronRecipeStatus.ACTIVE)
                .orElseThrow(() -> CauldronRecipeNotFoundException.EXCEPTION);
    }

    public Item getLockedCombinationResultItem(String... itemIds) {
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

    public List<Item> lockItems(String... itemIds) {
        return itemRepository.findAllByItemIdInForUpdateOrderByIdAsc(
                Arrays.stream(itemIds)
                        .distinct()
                        .toList()
        );
    }

    public void validateResultItemIsNotMaterial(CauldronRecipeRequest request) {
        if (request.getMaterialItemIds().contains(request.getResultItemId())) {
            throw CauldronRecipeResultItemInMaterialsException.EXCEPTION;
        }
    }

    public void validateNoDependencyCycle(Item resultItem, List<Item> materials) {
        cauldronRecipeDependencyValidator.validateNoCycle(
                resultItem.getId(),
                materials.stream()
                        .map(Item::getId)
                        .collect(Collectors.toSet())
        );
    }

    public List<Item> getMaterialItems(List<String> materialItemIds) {
        return materialItemIds.stream()
                .map(itemId -> itemRepository.findByItemId(itemId)
                        .orElseThrow(() -> ItemNotFoundException.EXCEPTION))
                .toList();
    }

    public CauldronRecipeResponse toResponse(CauldronRecipe recipe) {
        List<CauldronRecipeMaterial> materials = cauldronRecipeMaterialRepository
                .findAllWithItemByCauldronRecipeIdOrderBySlotNumberAsc(recipe.getId());
        return new CauldronRecipeResponse(recipe, materials);
    }
}
