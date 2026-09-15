package com.ufu.domain.cauldron.presentation;

import com.ufu.domain.cauldron.presentation.dto.request.CauldronRecipeRequest;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeDeleteResponse;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.service.CreateCauldronRecipeService;
import com.ufu.domain.cauldron.service.DeleteCauldronRecipeService;
import com.ufu.domain.cauldron.service.GetCauldronRecipeDetailService;
import com.ufu.domain.cauldron.service.GetCauldronRecipeListService;
import com.ufu.domain.cauldron.service.UpdateCauldronRecipeService;
import com.ufu.domain.itemsubmission.exception.AdminAccessDeniedException;
import com.ufu.domain.user.domain.Role;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.security.auth.CustomUserDetails;
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

@RestController
@RequestMapping("/api/admin/cauldron/recipes")
@RequiredArgsConstructor
public class AdminCauldronRecipeController {
    private final CreateCauldronRecipeService createCauldronRecipeService;
    private final GetCauldronRecipeListService getCauldronRecipeListService;
    private final GetCauldronRecipeDetailService getCauldronRecipeDetailService;
    private final UpdateCauldronRecipeService updateCauldronRecipeService;
    private final DeleteCauldronRecipeService deleteCauldronRecipeService;

    @PostMapping
    public CauldronRecipeResponse create(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CauldronRecipeRequest request
    ) {
        validateAdmin(customUserDetails);
        return createCauldronRecipeService.execute(request);
    }

    @GetMapping
    public List<CauldronRecipeResponse> getAll(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        validateAdmin(customUserDetails);
        return getCauldronRecipeListService.execute();
    }

    @GetMapping("/{recipeId}")
    public CauldronRecipeResponse get(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String recipeId
    ) {
        validateAdmin(customUserDetails);
        return getCauldronRecipeDetailService.execute(recipeId);
    }

    @PatchMapping("/{recipeId}")
    public CauldronRecipeResponse update(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String recipeId,
            @Valid @RequestBody CauldronRecipeRequest request
    ) {
        validateAdmin(customUserDetails);
        return updateCauldronRecipeService.execute(recipeId, request);
    }

    @DeleteMapping("/{recipeId}")
    public CauldronRecipeDeleteResponse delete(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String recipeId
    ) {
        validateAdmin(customUserDetails);
        return deleteCauldronRecipeService.execute(recipeId);
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
