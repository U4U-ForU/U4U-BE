package com.ufu.domain.gacha.presentation.dto.response;

import lombok.Getter;
import java.util.List;

@Getter
public class GachaPullResponse {
    private final List<GachaResultResponse> results;

    public GachaPullResponse(List<GachaResultResponse> results) {
        this.results = results;
    }
}
