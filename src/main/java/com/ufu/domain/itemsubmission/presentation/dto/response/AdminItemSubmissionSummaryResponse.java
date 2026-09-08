package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AdminItemSubmissionSummaryResponse {
    private final String submissionId;

    private final String name;

    private final String imageUrl;

    private final String submitterLoginId;

    private final ItemSubmissionStatus status;

    private final LocalDateTime createdAt;

    public AdminItemSubmissionSummaryResponse(ItemSubmission itemSubmission) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.name = itemSubmission.getName();
        this.imageUrl = itemSubmission.getImageUrl();
        this.submitterLoginId = itemSubmission.getSubmitter().getLoginId();
        this.status = itemSubmission.getStatus();
        this.createdAt = itemSubmission.getCreatedAt();
    }
}
