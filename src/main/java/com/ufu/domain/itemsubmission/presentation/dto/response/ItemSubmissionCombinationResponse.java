package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Schema(description = "아이템 제출 조합 처리 응답")
public class ItemSubmissionCombinationResponse {
    @Schema(description = "제출 식별값")
    private final String submissionId;

    @Schema(description = "제출 상태", example = "COMBINED")
    private final ItemSubmissionStatus status;

    @Schema(description = "조합 처리 시간")
    private final LocalDateTime combinedAt;

    @Schema(description = "생성된 조합 아이템 식별값")
    private final String itemId;

    public ItemSubmissionCombinationResponse(ItemSubmission itemSubmission, Item item) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.status = itemSubmission.getStatus();
        this.combinedAt = itemSubmission.getCombinedAt();
        this.itemId = item.getItemId();
    }
}
