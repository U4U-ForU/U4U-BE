package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ItemSubmissionCombinationResponse {
    private final String submissionId;

    private final ItemSubmissionStatus status;

    private final LocalDateTime combinedAt;

    private final String itemId;

    public ItemSubmissionCombinationResponse(ItemSubmission itemSubmission, Item item) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.status = itemSubmission.getStatus();
        this.combinedAt = itemSubmission.getCombinedAt();
        this.itemId = item.getItemId();
    }
}
