package com.ufu.domain.cauldron.presentation.dto.response;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CauldronRecipeDeleteResponse {
    private final String recipeId;

    private final CauldronRecipeStatus status;

    private final LocalDateTime deletedAt;

    public CauldronRecipeDeleteResponse(CauldronRecipe recipe) {
        this.recipeId = recipe.getRecipeId();
        this.status = recipe.getStatus();
        this.deletedAt = recipe.getDeletedAt();
    }
}
