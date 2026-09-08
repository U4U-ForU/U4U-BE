package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ItemSubmissionResponse {
    private final String submissionId;

    private final String name;

    private final String description;

    private final String imageUrl;

    private final ItemSubmissionStatus status;

    private final LocalDateTime createdAt;

    public ItemSubmissionResponse(ItemSubmission itemSubmission) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.name = itemSubmission.getName();
        this.description = itemSubmission.getDescription();
        this.imageUrl = itemSubmission.getImageUrl();
        this.status = itemSubmission.getStatus();
        this.createdAt = itemSubmission.getCreatedAt();
    }
}
