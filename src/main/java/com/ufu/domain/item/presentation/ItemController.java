package com.ufu.domain.item.presentation;

import com.ufu.domain.item.presentation.dto.request.ItemSortType;
import com.ufu.domain.item.presentation.dto.response.MyItemDetailResponse;
import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import com.ufu.domain.item.presentation.dto.response.MyTradingItemGroupResponse;
import com.ufu.domain.item.service.ItemService;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.error.ErrorResponse;
import com.ufu.global.security.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Item", description = "보유 아이템 조회 API")
@RestController
@RequestMapping("/api/items/me")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @Operation(summary = "내 보유 아이템 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 보유 아이템 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MyItemSummaryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<MyItemSummaryResponse> getMyItems(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "APPROVED_AT_DESC") ItemSortType sort,
            @RequestParam(required = false) String keyword
    ) {
        return itemService.getMyItems(getUserId(customUserDetails), sort, keyword);
    }

    @Operation(summary = "내 보유 아이템 상세 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 보유 아이템 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "보유 아이템을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{itemId}")
    public MyItemDetailResponse getMyItemDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String itemId
    ) {
        return itemService.getMyItemDetail(getUserId(customUserDetails), itemId);
    }

    @GetMapping("/trading")
    public List<MyTradingItemGroupResponse> getMyTradingItems(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        return itemService.getMyTradingItems(getUserId(customUserDetails));
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
