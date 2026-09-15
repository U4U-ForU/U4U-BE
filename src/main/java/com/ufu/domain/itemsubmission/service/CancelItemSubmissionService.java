package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotPendingException;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelItemSubmissionService {
    private final ItemSubmissionSupport itemSubmissionSupport;

    @Transactional
    public ItemSubmissionResponse execute(Long submitterId, String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionSupport.findSubmission(submissionId);

        itemSubmissionSupport.validateOwner(itemSubmission, submitterId);

        if (!itemSubmission.isPending()) {
            throw ItemSubmissionNotPendingException.EXCEPTION;
        }

        itemSubmission.cancel();
        return new ItemSubmissionResponse(itemSubmission);
    }
}
