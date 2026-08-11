package com.ufu.domain.cauldron.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "관리자 조합법 생성 및 수정 요청")
public class CauldronRecipeRequest {
    @NotBlank
    @Schema(description = "완성 조합 아이템 식별값")
    private String resultItemId;

    @NotNull
    @Size(min = 3, max = 3)
    @Schema(description = "재료 아이템 식별값 3개", example = "[\"item-id-1\", \"item-id-1\", \"item-id-2\"]")
    private List<@NotBlank String> materialItemIds;
}
