package com.ufu.domain.item.presentation;

import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.presentation.dto.response.AdminItemSummaryResponse;
import com.ufu.domain.item.service.GetAdminItemListService;
import com.ufu.domain.itemsubmission.exception.AdminAccessDeniedException;
import com.ufu.domain.user.domain.Role;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
public class AdminItemController {
    private final GetAdminItemListService getAdminItemListService;

    @GetMapping
    public List<AdminItemSummaryResponse> getItems(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) ItemStatus status
    ) {
        validateAdmin(customUserDetails);
        return getAdminItemListService.execute(status);
    }

    private void validateAdmin(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        if (customUserDetails.getUser().getRole() != Role.ADMIN) {
            throw AdminAccessDeniedException.EXCEPTION;
        }
    }
}
