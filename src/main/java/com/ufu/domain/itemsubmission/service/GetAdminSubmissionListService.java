package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.itemsubmission.presentation.dto.response.AdminItemSubmissionSummaryResponse;
import com.ufu.domain.itemsubmission.repository.ItemSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAdminSubmissionListService {
    private final ItemSubmissionRepository itemSubmissionRepository;

    @Transactional(readOnly = true)
    public List<AdminItemSubmissionSummaryResponse> execute() {
        return itemSubmissionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AdminItemSubmissionSummaryResponse::new)
                .toList();
    }
}
