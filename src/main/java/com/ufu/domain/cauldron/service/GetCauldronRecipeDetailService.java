package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCauldronRecipeDetailService {
    private final CauldronRecipeSupport cauldronRecipeSupport;

    @Transactional(readOnly = true)
    public CauldronRecipeResponse execute(String recipeId) {
        return cauldronRecipeSupport.toResponse(cauldronRecipeSupport.findActiveRecipe(recipeId));
    }
}
