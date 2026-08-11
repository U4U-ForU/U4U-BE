package com.ufu.domain.cauldron.presentation;

import com.ufu.domain.cauldron.presentation.dto.request.CauldronRecipeRequest;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeDeleteResponse;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.service.CauldronRecipeService;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Admin Cauldron Recipe", description = "관리자 조합법 관리 API")
@RestController
@RequestMapping("/api/admin/cauldron/recipes")
@RequiredArgsConstructor
public class AdminCauldronRecipeController {
    private final CauldronRecipeService cauldronRecipeService;

    @Operation(summary = "조합법 생성", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조합법 생성 성공"),
            @ApiResponse(responseCode = "400", description = "조합법 요청이 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public CauldronRecipeResponse create(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CauldronRecipeRequest request
    ) {
        validateAdmin(customUserDetails);
        return cauldronRecipeService.create(request);
    }

    @Operation(summary = "조합법 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조합법 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CauldronRecipeResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<CauldronRecipeResponse> getAll(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        validateAdmin(customUserDetails);
        return cauldronRecipeService.getAll();
    }

    @Operation(summary = "조합법 상세 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{recipeId}")
    public CauldronRecipeResponse get(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String recipeId
    ) {
        validateAdmin(customUserDetails);
        return cauldronRecipeService.get(recipeId);
    }

    @Operation(summary = "조합법 수정", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{recipeId}")
    public CauldronRecipeResponse update(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String recipeId,
            @Valid @RequestBody CauldronRecipeRequest request
    ) {
        validateAdmin(customUserDetails);
        return cauldronRecipeService.update(recipeId, request);
    }

    @Operation(summary = "조합법 삭제", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{recipeId}")
    public CauldronRecipeDeleteResponse delete(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String recipeId
    ) {
        validateAdmin(customUserDetails);
        return cauldronRecipeService.delete(recipeId);
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
