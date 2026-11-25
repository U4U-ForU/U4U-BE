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
    @Query("""
            select itemSubmission
            from ItemSubmission itemSubmission
            where itemSubmission.submitter.id = :submitterId
              and (:status is null or itemSubmission.status = :status)
            order by itemSubmission.createdAt desc
            """)
    List<ItemSubmission> findAllBySubmitterIdAndStatusOrderByCreatedAtDesc(
            @Param("submitterId") Long submitterId,
            @Param("status") ItemSubmissionStatus status
    );

    @Query("""
            select itemSubmission
            from ItemSubmission itemSubmission
            join fetch itemSubmission.submitter
            where (:status is null or itemSubmission.status = :status)
            order by itemSubmission.createdAt desc
            """)
    List<ItemSubmission> findAllWithSubmitterByStatusOrderByCreatedAtDesc(
            @Param("status") ItemSubmissionStatus status
    );

    Optional<ItemSubmission> findBySubmissionId(String submissionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select itemSubmission from ItemSubmission itemSubmission where itemSubmission.submissionId = :submissionId")
    Optional<ItemSubmission> findBySubmissionIdForUpdate(@Param("submissionId") String submissionId);

    boolean existsByNameAndStatusIn(String name, List<ItemSubmissionStatus> statuses);
}
