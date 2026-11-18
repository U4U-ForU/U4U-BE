package com.ufu.domain.itemsubmission.repository;

import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ItemSubmissionRepository extends JpaRepository<ItemSubmission, Long> {
    List<ItemSubmission> findAllBySubmitterIdOrderByCreatedAtDesc(Long submitterId);

    List<ItemSubmission> findAllByOrderByCreatedAtDesc();

    Optional<ItemSubmission> findBySubmissionId(String submissionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select itemSubmission from ItemSubmission itemSubmission where itemSubmission.submissionId = :submissionId")
    Optional<ItemSubmission> findBySubmissionIdForUpdate(@Param("submissionId") String submissionId);

    boolean existsByNameAndStatusIn(String name, List<ItemSubmissionStatus> statuses);
}
