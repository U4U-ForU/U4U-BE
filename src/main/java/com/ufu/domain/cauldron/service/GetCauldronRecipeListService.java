package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeMaterial;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.repository.CauldronRecipeMaterialRepository;
import com.ufu.domain.cauldron.repository.CauldronRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCauldronRecipeListService {
    private final CauldronRecipeRepository cauldronRecipeRepository;
    private final CauldronRecipeMaterialRepository cauldronRecipeMaterialRepository;

    @Transactional(readOnly = true)
    public List<CauldronRecipeResponse> execute() {
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
}
