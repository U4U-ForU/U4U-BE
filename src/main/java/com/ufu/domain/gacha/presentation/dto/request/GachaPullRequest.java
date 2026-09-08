package com.ufu.domain.gacha.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GachaPullRequest {
    @NotNull(message = "뽑기 횟수는 필수입니다")
    @Min(value = 1, message = "뽑기는 최소 1회부터 가능합니다")
    @Max(value = 10, message = "뽑기는 최대 10회까지 가능합니다")
    private Integer count;
}
