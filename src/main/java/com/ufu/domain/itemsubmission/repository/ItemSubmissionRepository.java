package com.ufu.domain.itemsubmission.repository;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemSubmissionRepository extends JpaRepository<ItemSubmission, Long> {
    List<ItemSubmission> findAllBySubmitterIdOrderByCreatedAtDesc(Long submitterId);

    List<ItemSubmission> findAllByOrderByCreatedAtDesc();

    Optional<ItemSubmission> findBySubmissionId(String submissionId);
}
