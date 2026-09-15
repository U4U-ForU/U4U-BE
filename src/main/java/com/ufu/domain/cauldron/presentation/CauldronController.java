package com.ufu.domain.cauldron.presentation;

import com.ufu.domain.cauldron.presentation.dto.request.CauldronMixRequest;
import com.ufu.domain.cauldron.presentation.dto.request.CauldronRecombineRequest;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronMixResponse;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecombineResponse;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.service.GetCauldronRecipeDetailService;
import com.ufu.domain.cauldron.service.GetCauldronRecipeListService;
import com.ufu.domain.cauldron.service.MixCauldronItemsService;
import com.ufu.domain.cauldron.service.RecombineCauldronRecipeService;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/cauldron")
@RequiredArgsConstructor
public class CauldronController {
    private final GetCauldronRecipeListService getCauldronRecipeListService;
    private final GetCauldronRecipeDetailService getCauldronRecipeDetailService;
    private final RecombineCauldronRecipeService recombineCauldronRecipeService;
    private final MixCauldronItemsService mixCauldronItemsService;

    @GetMapping("/recipes")
    public List<CauldronRecipeResponse> getAllRecipes() {
        return getCauldronRecipeListService.execute();
    }

    @GetMapping("/recipes/{recipeId}")
    public CauldronRecipeResponse getRecipe(@PathVariable String recipeId) {
        return getCauldronRecipeDetailService.execute(recipeId);
    }

    @PostMapping("/recombine")
    public CauldronRecombineResponse recombine(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CauldronRecombineRequest request
    ) {
        return recombineCauldronRecipeService.execute(getUserId(customUserDetails), request.getRecipeId());
    }

    @PostMapping("/mix")
    public CauldronMixResponse mix(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CauldronMixRequest request
    ) {
        return mixCauldronItemsService.execute(getUserId(customUserDetails), request.getMaterialItemIds());
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
