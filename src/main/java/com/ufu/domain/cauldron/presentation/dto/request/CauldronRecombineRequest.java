package com.ufu.domain.cauldron.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CauldronRecombineRequest {
    @NotBlank(message = "조합법 식별값은 필수입니다")
    private String recipeId;
}
