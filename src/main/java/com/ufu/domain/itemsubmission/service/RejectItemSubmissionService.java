package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotRejectableException;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionRejectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RejectItemSubmissionService {
    private final ItemSubmissionSupport itemSubmissionSupport;

    @Transactional
    public ItemSubmissionRejectionResponse execute(String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionSupport.findSubmission(submissionId);

        if (!itemSubmission.isPending()) {
            throw ItemSubmissionNotRejectableException.EXCEPTION;
        }

        itemSubmission.reject();
        return new ItemSubmissionRejectionResponse(itemSubmission);
    }
}
