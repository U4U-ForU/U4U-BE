package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeMaterial;
import com.ufu.domain.cauldron.exception.CauldronRecipeResultItemChangeForbiddenException;
import com.ufu.domain.cauldron.presentation.dto.request.CauldronRecipeRequest;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.repository.CauldronRecipeMaterialRepository;
import com.ufu.domain.item.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UpdateCauldronRecipeService {
    private final CauldronRecipeMaterialRepository cauldronRecipeMaterialRepository;
    private final CauldronRecipeSupport cauldronRecipeSupport;

    @Transactional
    public CauldronRecipeResponse execute(String recipeId, CauldronRecipeRequest request) {
        CauldronRecipe recipe = cauldronRecipeSupport.findActiveRecipeForUpdate(recipeId);
        validateResultItemIsUnchanged(recipe, request);
        Item resultItem = cauldronRecipeSupport.getLockedCombinationResultItem(recipe.getResultItem().getItemId());
        cauldronRecipeSupport.validateResultItemIsNotMaterial(request);
        List<Item> materials = cauldronRecipeSupport.getMaterialItems(request.getMaterialItemIds());
        cauldronRecipeSupport.validateNoDependencyCycle(resultItem, materials);

        changeMaterials(recipe, materials);

        return cauldronRecipeSupport.toResponse(recipe);
    }

    private void validateResultItemIsUnchanged(CauldronRecipe recipe, CauldronRecipeRequest request) {
        if (!recipe.getResultItem().getItemId().equals(request.getResultItemId())) {
            throw CauldronRecipeResultItemChangeForbiddenException.EXCEPTION;
        }
    }

    private void changeMaterials(CauldronRecipe recipe, List<Item> items) {
        List<CauldronRecipeMaterial> materials = cauldronRecipeMaterialRepository
                .findAllWithItemByCauldronRecipeIdOrderBySlotNumberAsc(recipe.getId());

        IntStream.range(0, CauldronRecipe.MATERIAL_COUNT)
                .forEach(index -> materials.get(index).changeItem(items.get(index)));
    }
}
