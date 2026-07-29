package com.ufu.domain.itemsubmission.presentation;

import com.ufu.domain.itemsubmission.exception.AdminAccessDeniedException;
import com.ufu.domain.itemsubmission.presentation.dto.response.AdminItemSubmissionDetailResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.AdminItemSubmissionSummaryResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionApprovalResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionRejectionResponse;
import com.ufu.domain.itemsubmission.service.ItemSubmissionService;
import com.ufu.domain.user.domain.Role;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.error.ErrorResponse;
import com.ufu.global.security.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Admin Item Submission", description = "관리자 아이템 제출 관리 API")
@RestController
@RequestMapping("/api/admin/items/submissions")
@RequiredArgsConstructor
public class AdminItemSubmissionController {
    private final ItemSubmissionService itemSubmissionService;

    @Operation(summary = "아이템 제출 전체 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이템 제출 전체 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<AdminItemSubmissionSummaryResponse> getSubmissions(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        validateAdmin(customUserDetails);
        return itemSubmissionService.getAdminSubmissions();
    }

    @Operation(summary = "아이템 제출 관리자 상세 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이템 제출 관리자 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "제출 내역을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{submissionId}")
    public AdminItemSubmissionDetailResponse getSubmission(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String submissionId
    ) {
        validateAdmin(customUserDetails);
        return itemSubmissionService.getAdminSubmission(submissionId);
    }

    @Operation(summary = "아이템 제출 승인", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이템 제출 승인 성공"),
            @ApiResponse(responseCode = "400", description = "승인할 수 없는 제출 상태",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "제출 내역을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{submissionId}/approve")
    public ItemSubmissionApprovalResponse approve(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String submissionId
    ) {
        validateAdmin(customUserDetails);
        return itemSubmissionService.approve(submissionId);
    }

    @Operation(summary = "아이템 제출 거절", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이템 제출 거절 성공"),
            @ApiResponse(responseCode = "400", description = "거절할 수 없는 제출 상태",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "제출 내역을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{submissionId}/reject")
    public ItemSubmissionRejectionResponse reject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String submissionId
    ) {
        validateAdmin(customUserDetails);
        return itemSubmissionService.reject(submissionId);
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
