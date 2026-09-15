package com.ufu.domain.item.presentation;

import com.ufu.domain.item.presentation.dto.request.ItemSortType;
import com.ufu.domain.item.presentation.dto.response.MyItemDetailResponse;
import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import com.ufu.domain.item.presentation.dto.response.MyTradingItemGroupResponse;
import com.ufu.domain.item.service.GetMyItemDetailService;
import com.ufu.domain.item.service.GetMyItemListService;
import com.ufu.domain.item.service.GetMyTradingItemListService;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/items/me")
@RequiredArgsConstructor
public class ItemController {
    private final GetMyItemListService getMyItemListService;
    private final GetMyItemDetailService getMyItemDetailService;
    private final GetMyTradingItemListService getMyTradingItemListService;

    @GetMapping
    public List<MyItemSummaryResponse> getMyItems(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "APPROVED_AT_DESC") ItemSortType sort,
            @RequestParam(required = false) String keyword
    ) {
        return getMyItemListService.execute(getUserId(customUserDetails), sort, keyword);
    }

    @GetMapping("/{itemId}")
    public MyItemDetailResponse getMyItemDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String itemId
    ) {
        return getMyItemDetailService.execute(getUserId(customUserDetails), itemId);
    }

    @GetMapping("/trading")
    public List<MyTradingItemGroupResponse> getMyTradingItems(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        return getMyTradingItemListService.execute(getUserId(customUserDetails));
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
