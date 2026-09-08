package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import lombok.Getter;

@Getter
public class ItemSubmissionRejectionResponse {
    private final String submissionId;

    private final ItemSubmissionStatus status;

    public ItemSubmissionRejectionResponse(ItemSubmission itemSubmission) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.status = itemSubmission.getStatus();
    }
}
