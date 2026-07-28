package com.ufu.domain.item.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.exception.InventoryItemNotFoundException;
import com.ufu.domain.item.presentation.dto.request.ItemSortType;
import com.ufu.domain.item.presentation.dto.response.MyItemDetailResponse;
import com.ufu.domain.item.presentation.dto.response.MyItemSummaryResponse;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.user.domain.Role;
import com.ufu.domain.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private UserItemRepository userItemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getMyItems_sortsByQuantityDescending() {
        User creator = createUser();
        UserItem oneItem = createUserItem(creator, "한 개 아이템", 1, LocalDateTime.of(2026, 7, 20, 10, 0));
        UserItem threeItems = createUserItem(creator, "세 개 아이템", 3, LocalDateTime.of(2026, 7, 21, 10, 0));

        given(userItemRepository.findAllByUserIdAndKeyword(1L, null))
                .willReturn(List.of(oneItem, threeItems));

        List<MyItemSummaryResponse> responses = itemService.getMyItems(
                1L,
                ItemSortType.QUANTITY_DESC,
                null
        );

        assertThat(responses)
                .extracting(MyItemSummaryResponse::getName)
                .containsExactly("세 개 아이템", "한 개 아이템");
    }

    @Test
    void getMyItemDetail_returnsApprovedAtAndCreatorLoginId() {
        User creator = createUser();
        LocalDateTime approvedAt = LocalDateTime.of(2026, 7, 21, 10, 0);
        UserItem userItem = createUserItem(creator, "우주 고양이", 2, approvedAt);

        given(userItemRepository.findByUserIdAndItemItemId(anyLong(), anyString()))
                .willReturn(Optional.of(userItem));

        MyItemDetailResponse response = itemService.getMyItemDetail(1L, userItem.getItem().getItemId());

        assertThat(response.getApprovedAt()).isEqualTo(approvedAt);
        assertThat(response.getCreatorLoginId()).isEqualTo("catmaker");
        assertThat(response.getQuantity()).isEqualTo(2);
    }

    @Test
    void getMyItemDetail_throwsExceptionWhenUserDoesNotOwnItem() {
        given(userItemRepository.findByUserIdAndItemItemId(anyLong(), anyString()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getMyItemDetail(1L, "unknown-item-id"))
                .isEqualTo(InventoryItemNotFoundException.EXCEPTION);
    }

    private User createUser() {
        return User.builder()
                .loginId("catmaker")
                .password("encoded-password")
                .email("catmaker@example.com")
                .nickname("고양이 제작자")
                .role(Role.USER)
                .build();
    }

    private UserItem createUserItem(User creator, String name, int quantity, LocalDateTime approvedAt) {
        Item item = Item.builder()
                .name(name)
                .description("아이템 설명")
                .imageUrl("/uploads/example.png")
                .creator(creator)
                .approvedAt(approvedAt)
                .build();

        return UserItem.builder()
                .user(creator)
                .item(item)
                .quantity(quantity)
                .build();
    }
}
