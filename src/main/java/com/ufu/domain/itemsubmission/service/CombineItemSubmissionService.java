package com.ufu.domain.itemsubmission.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.repository.ItemRepository;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.itemsubmission.domain.ItemSubmission;
import com.ufu.domain.itemsubmission.exception.ItemSubmissionNotCombinableException;
import com.ufu.domain.itemsubmission.presentation.dto.response.ItemSubmissionCombinationResponse;
import com.ufu.domain.user.domain.User;
import com.ufu.domain.user.exception.UserNotFoundException;
import com.ufu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CombineItemSubmissionService {
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;
    private final ItemSubmissionSupport itemSubmissionSupport;

    @Transactional
    public ItemSubmissionCombinationResponse execute(String submissionId) {
        ItemSubmission itemSubmission = itemSubmissionSupport.findSubmission(submissionId);

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
}
