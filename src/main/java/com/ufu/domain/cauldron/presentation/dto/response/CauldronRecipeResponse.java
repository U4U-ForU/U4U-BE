package com.ufu.domain.cauldron.presentation.dto.response;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeMaterial;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import com.ufu.domain.item.presentation.dto.response.AdminItemSummaryResponse;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CauldronRecipeResponse {
    private final String recipeId;

    private final AdminItemSummaryResponse resultItem;

    private final List<AdminItemSummaryResponse> materials;

    private final CauldronRecipeStatus status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private final LocalDateTime deletedAt;

    public CauldronRecipeResponse(CauldronRecipe recipe, List<CauldronRecipeMaterial> materials) {
        this.recipeId = recipe.getRecipeId();
        this.resultItem = new AdminItemSummaryResponse(recipe.getResultItem());
        this.materials = materials.stream()
                .map(material -> new AdminItemSummaryResponse(material.getItem()))
                .toList();
        this.status = recipe.getStatus();
        this.createdAt = recipe.getCreatedAt();
        this.updatedAt = recipe.getUpdatedAt();
        this.deletedAt = recipe.getDeletedAt();
    }
}
