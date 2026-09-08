package com.ufu.domain.cauldron.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class CauldronRecipeRequest {
    @NotBlank
    private String resultItemId;

    @NotNull
    @Size(min = 3, max = 3)
    private List<@NotBlank String> materialItemIds;
}
