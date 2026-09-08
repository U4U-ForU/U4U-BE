package com.ufu.domain.itemsubmission.presentation;

import com.ufu.domain.itemsubmission.exception.AdminAccessDeniedException;
import com.ufu.domain.itemsubmission.presentation.dto.response.AdminItemSubmissionDetailResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.AdminItemSubmissionSummaryResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionApprovalResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionCombinationResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionRejectionResponse;
import com.ufu.domain.itemsubmission.service.ItemSubmissionService;
import com.ufu.domain.user.domain.Role;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/admin/items/submissions")
@RequiredArgsConstructor
public class AdminItemSubmissionController {
    private final ItemSubmissionService itemSubmissionService;

    @GetMapping
    public List<AdminItemSubmissionSummaryResponse> getSubmissions(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        validateAdmin(customUserDetails);
        return itemSubmissionService.getAdminSubmissions();
    }

    @GetMapping("/{submissionId}")
    public AdminItemSubmissionDetailResponse getSubmission(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String submissionId
    ) {
        validateAdmin(customUserDetails);
        return itemSubmissionService.getAdminSubmission(submissionId);
    }

    @PatchMapping("/{submissionId}/approve")
    public ItemSubmissionApprovalResponse approve(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String submissionId
    ) {
        validateAdmin(customUserDetails);
        return itemSubmissionService.approve(submissionId);
    }

    @PatchMapping("/{submissionId}/combine")
    public ItemSubmissionCombinationResponse combine(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String submissionId
    ) {
        validateAdmin(customUserDetails);
        return itemSubmissionService.combine(submissionId);
    }

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
