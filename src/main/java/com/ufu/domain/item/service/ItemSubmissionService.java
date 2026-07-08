package com.ufu.domain.item.service;

import com.ufu.domain.item.domain.ItemSubmission;
import com.ufu.domain.item.presentation.dto.request.ItemSubmissionRequest;
import com.ufu.domain.item.presentation.dto.response.ItemSubmissionResponse;
import com.ufu.domain.item.repository.ItemSubmissionRepository;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import com.ufu.global.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemSubmissionService {
    private final ItemSubmissionRepository itemSubmissionRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional
    public ItemSubmissionResponse submit(Long submitterId, ItemSubmissionRequest request) {
        User submitter = userRepository.findById(submitterId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
        String imageUrl = storageService.store(request.getImage());

        ItemSubmission itemSubmission = ItemSubmission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .submitter(submitter)
                .build();

        return new ItemSubmissionResponse(itemSubmissionRepository.save(itemSubmission));
    }

    @Transactional(readOnly = true)
    public List<ItemSubmissionResponse> getMySubmissions(Long submitterId) {
        return itemSubmissionRepository.findAllBySubmitterIdOrderByCreatedAtDesc(submitterId)
                .stream()
                .map(ItemSubmissionResponse::new)
                .toList();
    }
}
