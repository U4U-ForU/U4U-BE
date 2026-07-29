package com.ufu.domain.itemsubmission.presentation.dto.response;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Schema(description = "관리자 아이템 제출 목록 응답")
public class AdminItemSubmissionSummaryResponse {
    @Schema(description = "제출 식별값")
    private final String submissionId;

    @Schema(description = "아이템 이름")
    private final String name;

    @Schema(description = "아이템 이미지 URL")
    private final String imageUrl;

    @Schema(description = "제출자 로그인 아이디")
    private final String submitterLoginId;

    @Schema(description = "제출 상태", example = "PENDING")
    private final ItemSubmissionStatus status;

    @Schema(description = "제출 시간")
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
