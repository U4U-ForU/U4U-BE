package com.ufu.domain.cauldron.service;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.UserItem;
import com.ufu.domain.item.repository.UserItemRepository;
import com.ufu.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 가마솥 유스케이스들이 공유하는 사용자 보유 아이템 변경 헬퍼다.
 *
 * <p><b>이 클래스에는 절대 {@code @Transactional}을 붙이지 않는다.</b>
 * 비관적 쓰기 락으로 보유 아이템을 조회한 뒤 수량을 변경하므로,
 * 호출자의 트랜잭션 안에서 실행되어야 한다. 자체 트랜잭션을 열면 락이 즉시 풀리고
 * 수량 변경만 부분 커밋된다. 컨트롤러에 직접 주입해서도 안 된다.
 * ({@code TradeTransactionService}와 동일한 규약이다.)
 */
@Service
@RequiredArgsConstructor
public class CauldronUserItemSupport {
    private final UserItemRepository userItemRepository;

    public UserItem addResultItem(User user, Item resultItem) {
        return userItemRepository.findByUserIdAndItemIdForUpdate(user.getId(), resultItem.getId())
                .map(userItem -> {
                    userItem.increaseQuantity(1);
                    return userItem;
                })
                .orElseGet(() -> userItemRepository.save(UserItem.builder()
                        .user(user)
                        .item(resultItem)
                        .quantity(1)
                        .build()));
    }
}
