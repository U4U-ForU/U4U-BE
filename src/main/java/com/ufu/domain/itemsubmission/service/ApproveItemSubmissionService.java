package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.repository.ItemRepository;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotApprovableException;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionApprovalResponse;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApproveItemSubmissionService {
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;
    private final ItemSubmissionSupport itemSubmissionSupport;

    @Transactional
    public ItemSubmissionApprovalResponse execute(String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionSupport.findSubmission(submissionId);

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
}
