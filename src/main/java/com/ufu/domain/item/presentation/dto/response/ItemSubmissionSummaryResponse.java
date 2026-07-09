package com.ufu.domain.item.presentation.dto.response;

import com.ufu.domain.item.domain.ItemSubmission;
import com.ufu.domain.item.domain.ItemSubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Schema(description = "아이템 제출 목록 응답")
public class ItemSubmissionSummaryResponse {
    @Schema(description = "제출 식별값", example = "31f0a67a-85c5-4ec9-bc26-7464480f73e0")
    private final String submissionId;

    @Schema(description = "아이템 이름", example = "우주 고양이")
    private final String name;

    @Schema(description = "아이템 이미지 URL")
    private final String imageUrl;

    @Schema(description = "제출 상태", example = "PENDING")
    private final ItemSubmissionStatus status;

    @Schema(description = "생성 시간")
    private final LocalDateTime createdAt;

    public ItemSubmissionSummaryResponse(ItemSubmission itemSubmission) {
        this.submissionId = itemSubmission.getSubmissionId();
        this.name = itemSubmission.getName();
        this.imageUrl = itemSubmission.getImageUrl();
        this.status = itemSubmission.getStatus();
        this.createdAt = itemSubmission.getCreatedAt();
    }
}
