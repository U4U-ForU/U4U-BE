package com.ufu.domain.gacha.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "아이템 뽑기 요청")
public class GachaPullRequest {
    @NotNull(message = "뽑기 횟수는 필수입니다")
    @Min(value = 1, message = "뽑기는 최소 1회부터 가능합니다")
    @Max(value = 10, message = "뽑기는 최대 10회까지 가능합니다")
    @Schema(description = "뽑기 횟수", example = "5", minimum = "1", maximum = "10")
    private Integer count;
}
