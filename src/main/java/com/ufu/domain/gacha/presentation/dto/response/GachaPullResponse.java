package com.ufu.domain.gacha.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.util.List;

@Getter
@Schema(description = "아이템 뽑기 결과 모달 응답")
public class GachaPullResponse {
    @Schema(description = "뽑기 결과")
    private final List<GachaResultResponse> results;

    public GachaPullResponse(List<GachaResultResponse> results) {
        this.results = results;
    }
}
