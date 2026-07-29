package com.ufu.domain.itemsubmission.domain;

import com.ufu.domain.user.domain.User;
import com.ufu.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "item_submission_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSubmission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_submission_id", nullable = false)
    private Long id;

    @Column(name = "submission_id", nullable = false, unique = true, length = 36)
    private String submissionId;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "image_url", nullable = false, length = 2048)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ItemSubmissionStatus status;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id", nullable = false)
    private User submitter;

    @Builder
    private ItemSubmission(String name, String description, String imageUrl, User submitter) {
        this.submissionId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.submitter = submitter;
        this.status = ItemSubmissionStatus.PENDING;
    }

    public boolean isSubmittedBy(Long userId) {
        return submitter.getId().equals(userId);
    }

    public boolean isPending() {
        return status == ItemSubmissionStatus.PENDING;
    }

    public void cancel() {
        this.status = ItemSubmissionStatus.CANCELED;
    }

    public void approve(LocalDateTime approvedAt) {
        this.status = ItemSubmissionStatus.APPROVED;
        this.approvedAt = approvedAt;
    }

    public void reject() {
        this.status = ItemSubmissionStatus.REJECTED;
    }
}
