package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Schema(description = "아이템 제출 승인 응답")
public class ItemSubmissionApprovalResponse {
    @Schema(description = "제출 식별값", example = "31f0a67a-85c5-4ec9-bc26-7464480f73e0")
    private final String submissionId;

    @Schema(description = "제출 상태", example = "APPROVED")
    private final ItemSubmissionStatus status;

    @Schema(description = "승인 시간")
    private final LocalDateTime approvedAt;

    @Schema(description = "생성된 아이템 식별값", example = "31f0a67a-85c5-4ec9-bc26-7464480f73e0")
    private final String itemId;

    public ItemSubmissionApprovalResponse(ItemSubmission itemSubmission, Item item) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.status = itemSubmission.getStatus();
        this.approvedAt = itemSubmission.getApprovedAt();
        this.itemId = item.getItemId();
    }
}
