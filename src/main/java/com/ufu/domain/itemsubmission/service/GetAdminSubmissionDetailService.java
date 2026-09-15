package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.presentation.dto.response.AdminItemSubmissionDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAdminSubmissionDetailService {
    private final ItemSubmissionSupport itemSubmissionSupport;

    @Transactional(readOnly = true)
    public AdminItemSubmissionDetailResponse execute(String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionSupport.findSubmission(submissionId);

        return new AdminItemSubmissionDetailResponse(itemSubmission);
    }
}
