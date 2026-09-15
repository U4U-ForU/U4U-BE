package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeDeleteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeleteCauldronRecipeService {
    private final CauldronRecipeSupport cauldronRecipeSupport;

    @Transactional
    public CauldronRecipeDeleteResponse execute(String recipeId) {
        CauldronRecipe recipe = cauldronRecipeSupport.findActiveRecipeForUpdate(recipeId);
        cauldronRecipeSupport.lockItems(recipe.getResultItem().getItemId());
        recipe.delete(LocalDateTime.now());
        return new CauldronRecipeDeleteResponse(recipe);
    }
}
