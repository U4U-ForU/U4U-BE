package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.repository.ItemRepository;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionForbiddenException;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNameAlreadyExistsException;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotApprovableException;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotCombinableException;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotFoundException;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotPendingException;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotRejectableException;
import com.ufu.domain.itemsubmission.presentation.dto.request.ItemSubmissionRequest;
import com.ufu.domain.itemsubmission.presentation.dto.response.AdminItemSubmissionDetailResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.AdminItemSubmissionSummaryResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionApprovalResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionCombinationResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionRejectionResponse;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionSummaryResponse;
import com.ufu.domain.itemsubmission.repository.ItemSubmissionRepository;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import com.ufu.global.S3.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemSubmissionService {
    private static final List<ItemSubmissionStatus> RESERVED_NAME_STATUSES = List.of(
            ItemSubmissionStatus.PENDING,
            ItemSubmissionStatus.APPROVED,
            ItemSubmissionStatus.COMBINED
    );

    private final ItemSubmissionRepository itemSubmissionRepository;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;
    private final S3Util s3Util;

    @Transactional
    public ItemSubmissionResponse submit(Long submitterId, ItemSubmissionRequest request) {
        User submitter = userRepository.findById(submitterId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        validateNameIsAvailable(request.getName());
        String imageUrl = s3Util.upload(request.getImage(), "submission");

        ItemSubmission itemSubmission = ItemSubmission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .submitter(submitter)
                .build();

        try {
            return new ItemSubmissionResponse(itemSubmissionRepository.saveAndFlush(itemSubmission));
        } catch (DataIntegrityViolationException exception) {
            throw ItemSubmissionNameAlreadyExistsException.EXCEPTION;
        }
    }

    private void validateNameIsAvailable(String name) {
        if (itemRepository.existsByName(name)
                || itemSubmissionRepository.existsByNameAndStatusIn(name, RESERVED_NAME_STATUSES)) {
            throw ItemSubmissionNameAlreadyExistsException.EXCEPTION;
        }
    }

    @Transactional(readOnly = true)
    public List<ItemSubmissionSummaryResponse> getMySubmissions(Long submitterId) {
        return itemSubmissionRepository.findAllBySubmitterIdOrderByCreatedAtDesc(submitterId)
                .stream()
                .map(ItemSubmissionSummaryResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemSubmissionResponse getMySubmission(Long submitterId, String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ItemSubmissionNotFoundException.EXCEPTION);

        if (!itemSubmission.isSubmittedBy(submitterId)) {
            throw ItemSubmissionForbiddenException.EXCEPTION;
        }

        return new ItemSubmissionResponse(itemSubmission);
    }

    @Transactional
    public ItemSubmissionResponse cancel(Long submitterId, String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ItemSubmissionNotFoundException.EXCEPTION);

        if (!itemSubmission.isSubmittedBy(submitterId)) {
            throw ItemSubmissionForbiddenException.EXCEPTION;
        }

        if (!itemSubmission.isPending()) {
            throw ItemSubmissionNotPendingException.EXCEPTION;
        }

        itemSubmission.cancel();
        return new ItemSubmissionResponse(itemSubmission);
    }

    @Transactional(readOnly = true)
    public List<AdminItemSubmissionSummaryResponse> getAdminSubmissions() {
        return itemSubmissionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AdminItemSubmissionSummaryResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminItemSubmissionDetailResponse getAdminSubmission(String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ItemSubmissionNotFoundException.EXCEPTION);

        return new AdminItemSubmissionDetailResponse(itemSubmission);
    }

    @Transactional
    public ItemSubmissionApprovalResponse approve(String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ItemSubmissionNotFoundException.EXCEPTION);

        if (!itemSubmission.isPending()) {
            throw ItemSubmissionNotApprovableException.EXCEPTION;
        }

        itemSubmission.approve(LocalDateTime.now());

        Item item = Item.builder()
                .name(itemSubmission.getName())
                .description(itemSubmission.getDescription())
                .imageUrl(itemSubmission.getImageUrl())
                .status(ItemStatus.GACHA)
                .creator(itemSubmission.getSubmitter())
                .approvedAt(itemSubmission.getApprovedAt())
                .build();
        itemRepository.save(item);

        User user = userRepository.findByIdForUpdate(itemSubmission.getSubmitter().getId())
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        UserItem userItem = UserItem.builder()
                .user(user)
                .item(item)
                .quantity(1)
                .build();
        userItemRepository.save(userItem);

        return new ItemSubmissionApprovalResponse(itemSubmission, item);
    }

    @Transactional
    public ItemSubmissionCombinationResponse combine(String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ItemSubmissionNotFoundException.EXCEPTION);

        if (!itemSubmission.isPending()) {
            throw ItemSubmissionNotCombinableException.EXCEPTION;
        }

        itemSubmission.combine(LocalDateTime.now());

        Item item = Item.builder()
                .name(itemSubmission.getName())
                .description(itemSubmission.getDescription())
                .imageUrl(itemSubmission.getImageUrl())
                .status(ItemStatus.COMBINATION)
                .creator(itemSubmission.getSubmitter())
                .approvedAt(itemSubmission.getCombinedAt())
                .build();
        itemRepository.save(item);

        User user = userRepository.findByIdForUpdate(itemSubmission.getSubmitter().getId())
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        UserItem userItem = UserItem.builder()
                .user(user)
                .item(item)
                .quantity(1)
                .build();
        userItemRepository.save(userItem);

        return new ItemSubmissionCombinationResponse(itemSubmission, item);
    }

    @Transactional
    public ItemSubmissionRejectionResponse reject(String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ItemSubmissionNotFoundException.EXCEPTION);

        if (!itemSubmission.isPending()) {
            throw ItemSubmissionNotRejectableException.EXCEPTION;
        }

        itemSubmission.reject();
        return new ItemSubmissionRejectionResponse(itemSubmission);
    }
}
