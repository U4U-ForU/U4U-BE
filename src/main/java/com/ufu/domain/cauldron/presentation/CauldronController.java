package com.ufu.domain.cauldron.presentation;

import com.ufu.domain.cauldron.presentation.dto.request.CauldronRecombineRequest;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecombineResponse;
import com.ufu.domain.cauldron.presentation.dto.response.CauldronRecipeResponse;
import com.ufu.domain.cauldron.service.CauldronRecipeService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Cauldron", description = "사용자 가마솥 API")
@RestController
@RequestMapping("/api/cauldron")
@RequiredArgsConstructor
public class CauldronController {
    private final CauldronRecipeService cauldronRecipeService;

    @Operation(summary = "활성 조합법 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "활성 조합법 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CauldronRecipeResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/recipes")
    public List<CauldronRecipeResponse> getAllRecipes() {
        return cauldronRecipeService.getAll();
    }

    @Operation(summary = "활성 조합법 상세 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "활성 조합법 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "조합법을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/recipes/{recipeId}")
    public CauldronRecipeResponse getRecipe(@PathVariable String recipeId) {
        return cauldronRecipeService.get(recipeId);
    }

    @Operation(summary = "재조합 실행", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재조합 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 또는 재료 아이템 수량 부족",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "조합법을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/recombine")
    public CauldronRecombineResponse recombine(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CauldronRecombineRequest request
    ) {
        return cauldronRecipeService.recombine(getUserId(customUserDetails), request.getRecipeId());
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
