package com.ufu.domain.item.presentation;

import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.presentation.dto.response.AdminItemSummaryResponse;
import com.ufu.domain.item.service.ItemService;
import com.ufu.domain.itemsubmission.exception.AdminAccessDeniedException;
import com.ufu.domain.user.domain.Role;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Admin Item", description = "관리자 아이템 조회 API")
@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
public class AdminItemController {
    private final ItemService itemService;

    @Operation(summary = "관리자 아이템 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "관리자 아이템 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AdminItemSummaryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<AdminItemSummaryResponse> getItems(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) ItemStatus status
    ) {
        validateAdmin(customUserDetails);
        return itemService.getAdminItems(status);
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
