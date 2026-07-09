package com.ufu.domain.item.repository;

import com.ufu.domain.item.domain.ItemSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemSubmissionRepository extends JpaRepository<ItemSubmission, Long> {
    List<ItemSubmission> findAllBySubmitterIdOrderByCreatedAtDesc(Long submitterId);

    Optional<ItemSubmission> findBySubmissionId(String submissionId);
}
