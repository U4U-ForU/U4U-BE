package com.ufu.domain.item.repository;

import com.ufu.domain.item.domain.ItemSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemSubmissionRepository extends JpaRepository<ItemSubmission, Long> {
}
