package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ItemSubmissionSummaryResponse {
    private final String submissionId;

    private final String name;

    private final String imageUrl;

    private final ItemSubmissionStatus status;

    private final LocalDateTime createdAt;

    public ItemSubmissionSummaryResponse(ItemSubmission itemSubmission) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.name = itemSubmission.getName();
        this.imageUrl = itemSubmission.getImageUrl();
        this.status = itemSubmission.getStatus();
        this.createdAt = itemSubmission.getCreatedAt();
    }
}
