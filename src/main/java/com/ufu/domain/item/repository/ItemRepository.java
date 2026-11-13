package com.ufu.domain.item.repository;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByItemId(String itemId);

    boolean existsByName(String name);

    List<Item> findAllByItemIdIn(List<String> itemIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select item
            from Item item
            where item.itemId in :itemIds
            order by item.id asc
            """)
    List<Item> findAllByItemIdInForUpdateOrderByIdAsc(@Param("itemIds") List<String> itemIds);

    List<Item> findAllByStatusOrderByApprovedAtDesc(ItemStatus status);

    List<Item> findAllByStatusAndItemIdNotIn(ItemStatus status, List<String> itemIds);

    @Query("""
            select item
            from Item item
            join fetch item.creator creator
            where (:status is null or item.status = :status)
              and (:keyword is null
                   or lower(item.name) like lower(concat('%', :keyword, '%'))
                   or lower(creator.loginId) like lower(concat('%', :keyword, '%')))
            """) // 검색 조건이 null -> 통과 아닐 경우 -> 뒤에 조건.
    List<Item> findAllWithCreatorByStatusAndKeyword(
            @Param("status") ItemStatus status,
            @Param("keyword") String keyword
    );
}
