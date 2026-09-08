package com.ufu.domain.gacha.presentation.dto.response;

import com.ufu.domain.user.domain.User;
import lombok.Getter;

@Getter
public class GachaCurrencyResponse {
    private final int currency;

    public GachaCurrencyResponse(User user) {
        this.currency = user.getCurrency();
    }
}
