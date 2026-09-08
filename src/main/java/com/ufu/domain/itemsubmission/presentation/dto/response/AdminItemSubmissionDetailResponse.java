package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AdminItemSubmissionDetailResponse {
    private final String submissionId;

    private final String name;

    private final String description;

    private final String imageUrl;

    private final String submitterLoginId;

    private final ItemSubmissionStatus status;

    private final LocalDateTime createdAt;

    private final LocalDateTime approvedAt;

    private final LocalDateTime combinedAt;

    public AdminItemSubmissionDetailResponse(ItemSubmission itemSubmission) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.name = itemSubmission.getName();
        this.description = itemSubmission.getDescription();
        this.imageUrl = itemSubmission.getImageUrl();
        this.submitterLoginId = itemSubmission.getSubmitter().getLoginId();
        this.status = itemSubmission.getStatus();
        this.createdAt = itemSubmission.getCreatedAt();
        this.approvedAt = itemSubmission.getApprovedAt();
        this.combinedAt = itemSubmission.getCombinedAt();
    }
}
