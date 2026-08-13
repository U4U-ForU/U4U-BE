package com.ufu.domain.cauldron.presentation.dto.response;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeMaterial;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import com.ufu.domain.item.presentation.dto.response.AdminItemSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(description = "조합법 응답")
public class CauldronRecipeResponse {
    @Schema(description = "조합법 식별값")
    private final String recipeId;

    @Schema(description = "완성 아이템")
    private final AdminItemSummaryResponse resultItem;

    @Schema(description = "재료 아이템 3개")
    private final List<AdminItemSummaryResponse> materials;

    @Schema(description = "조합법 상태", example = "ACTIVE")
    private final CauldronRecipeStatus status;

    @Schema(description = "생성 시간")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private final LocalDateTime updatedAt;

    @Schema(description = "삭제 시간")
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
