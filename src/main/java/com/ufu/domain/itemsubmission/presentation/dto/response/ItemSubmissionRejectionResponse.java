package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "아이템 제출 거절 응답")
public class ItemSubmissionRejectionResponse {
    @Schema(description = "제출 식별값")
    private final String submissionId;

    @Schema(description = "제출 상태", example = "REJECTED")
    private final ItemSubmissionStatus status;

    public ItemSubmissionRejectionResponse(ItemSubmission itemSubmission) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.status = itemSubmission.getStatus();
    }
}
