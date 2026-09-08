package com.ufu.domain.itemsubmission.presentation;

import com.ufu.domain.itemsubmission.presentation.dto.request.ItemSubmissionRequest;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionSummaryResponse;
import com.ufu.domain.itemsubmission.service.ItemSubmissionService;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.global.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/items/submissions")
@RequiredArgsConstructor
public class ItemSubmissionController {
    private final ItemSubmissionService itemSubmissionService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ItemSubmissionResponse submit(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @ModelAttribute ItemSubmissionRequest request
    ) {
        return itemSubmissionService.submit(getUserId(customUserDetails), request);
    }

    @GetMapping("/me")
    public List<ItemSubmissionSummaryResponse> getMySubmissions(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        return itemSubmissionService.getMySubmissions(getUserId(customUserDetails));
    }

    @GetMapping("/{submissionId}")
    public ItemSubmissionResponse getMySubmission(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String submissionId
    ) {
        return itemSubmissionService.getMySubmission(getUserId(customUserDetails), submissionId);
    }

    @PatchMapping("/{submissionId}/cancel")
    public ItemSubmissionResponse cancel(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String submissionId
    ) {
        return itemSubmissionService.cancel(getUserId(customUserDetails), submissionId);
    }

    private Long getUserId(CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw UserNotFoundException.EXCEPTION;
        }

        return customUserDetails.getUser().getId();
    }
}
