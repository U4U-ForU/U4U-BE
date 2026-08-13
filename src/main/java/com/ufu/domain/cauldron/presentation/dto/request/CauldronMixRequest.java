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
@Schema(description = "섞기 실행 요청")
public class CauldronMixRequest {
    @NotNull(message = "섞기 재료는 필수입니다")
    @Size(min = 4, max = 4, message = "섞기 재료는 정확히 4개여야 합니다")
    @Schema(description = "소비할 재료 아이템 식별값 4개")
    private List<@NotBlank(message = "재료 아이템 식별값은 필수입니다") String> materialItemIds;
}
