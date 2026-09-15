package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionSummaryResponse;
import com.ufu.domain.itemsubmission.repository.ItemSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMySubmissionListService {
    private final ItemSubmissionRepository itemSubmissionRepository;

    @Transactional(readOnly = true)
    public List<ItemSubmissionSummaryResponse> execute(Long submitterId) {
        return itemSubmissionRepository.findAllBySubmitterIdOrderByCreatedAtDesc(submitterId)
                .stream()
                .map(ItemSubmissionSummaryResponse::new)
                .toList();
    }
}
