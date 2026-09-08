package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ItemSubmissionApprovalResponse {
    private final String submissionId;

    private final ItemSubmissionStatus status;

    private final LocalDateTime approvedAt;

    private final String itemId;

    public ItemSubmissionApprovalResponse(ItemSubmission itemSubmission, Item item) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.status = itemSubmission.getStatus();
        this.approvedAt = itemSubmission.getApprovedAt();
        this.itemId = item.getItemId();
    }
}
