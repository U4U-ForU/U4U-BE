package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.item.repository.ItemRepository;
import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.domain.ItemSubmissionStatus;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNameAlreadyExistsException;
import com.ufu.domain.itemsubmission.presentation.dto.request.ItemSubmissionRequest;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionResponse;
import com.ufu.domain.itemsubmission.repository.ItemSubmissionRepository;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import com.ufu.global.S3.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmitItemSubmissionService {
    private static final List<ItemSubmissionStatus> RESERVED_NAME_STATUSES = List.of(
            ItemSubmissionStatus.PENDING,
            ItemSubmissionStatus.APPROVED,
            ItemSubmissionStatus.COMBINED
    );

    private final ItemSubmissionRepository itemSubmissionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final S3Util s3Util;

    @Transactional
    public ItemSubmissionResponse execute(Long submitterId, ItemSubmissionRequest request) {
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
}
