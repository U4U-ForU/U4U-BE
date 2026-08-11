package com.ufu.domain.cauldron.presentation.dto.response;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Schema(description = "관리자 조합법 삭제 응답")
public class CauldronRecipeDeleteResponse {
    @Schema(description = "조합법 식별값")
    private final String recipeId;

    @Schema(description = "조합법 상태", example = "DELETED")
    private final CauldronRecipeStatus status;

    @Schema(description = "삭제 시간")
    private final LocalDateTime deletedAt;

    public CauldronRecipeDeleteResponse(CauldronRecipe recipe) {
        this.recipeId = recipe.getRecipeId();
        this.status = recipe.getStatus();
        this.deletedAt = recipe.getDeletedAt();
    }
}
