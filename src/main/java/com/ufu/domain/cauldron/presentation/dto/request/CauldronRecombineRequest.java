package com.ufu.domain.cauldron.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "재조합 실행 요청")
public class CauldronRecombineRequest {
    @NotBlank(message = "조합법 식별값은 필수입니다")
    @Schema(description = "실행할 조합법 식별값")
    private String recipeId;
}
