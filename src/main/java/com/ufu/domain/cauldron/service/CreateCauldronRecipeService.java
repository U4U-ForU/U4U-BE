package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeMaterial;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import com.ufu.domain.cauldron.exception.CauldronRecipeResultItemAlreadyExistsException;
import com.ufu.domain.cauldron.presentation.dto.request.CauldronRecipeRequest;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.repository.CauldronRecipeMaterialRepository;
import com.ufu.domain.cauldron.repository.CauldronRecipeRepository;
import com.ufu.domain.item.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CreateCauldronRecipeService {
    private final CauldronRecipeRepository cauldronRecipeRepository;
    private final CauldronRecipeMaterialRepository cauldronRecipeMaterialRepository;
    private final CauldronRecipeSupport cauldronRecipeSupport;

    @Transactional
    public CauldronRecipeResponse execute(CauldronRecipeRequest request) {
        Item resultItem = cauldronRecipeSupport.getLockedCombinationResultItem(request.getResultItemId());
        cauldronRecipeSupport.validateResultItemIsNotMaterial(request);
        List<Item> materials = cauldronRecipeSupport.getMaterialItems(request.getMaterialItemIds());
        validateNoActiveRecipeForResultItem(resultItem);
        cauldronRecipeSupport.validateNoDependencyCycle(resultItem, materials);

        CauldronRecipe recipe = cauldronRecipeRepository.save(CauldronRecipe.builder()
                .resultItem(resultItem)
                .build());
        saveMaterials(recipe, materials);

        return cauldronRecipeSupport.toResponse(recipe);
    }

    private void validateNoActiveRecipeForResultItem(Item resultItem) {
        if (cauldronRecipeRepository.existsByResultItemAndStatus(resultItem, CauldronRecipeStatus.ACTIVE)) {
            throw CauldronRecipeResultItemAlreadyExistsException.EXCEPTION;
        }
    }

    private void saveMaterials(CauldronRecipe recipe, List<Item> materials) {
        List<CauldronRecipeMaterial> recipeMaterials = IntStream.range(0, CauldronRecipe.MATERIAL_COUNT)
                .mapToObj(index -> CauldronRecipeMaterial.builder()
                        .cauldronRecipe(recipe)
                        .item(materials.get(index))
                        .slotNumber(index + 1)
                        .build())
                .toList();

        cauldronRecipeMaterialRepository.saveAll(recipeMaterials);
    }
}
