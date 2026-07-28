package com.ufu.domain.gacha.presentation.dto.response;

import com.ufu.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "보유 재화 조회 응답")
public class GachaCurrencyResponse {
    @Schema(description = "현재 보유 재화", example = "20")
    private final int currency;

    public GachaCurrencyResponse(User user) {
        this.currency = user.getCurrency();
    }
}
