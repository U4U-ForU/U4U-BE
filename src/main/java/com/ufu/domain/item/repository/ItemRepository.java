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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select item
            from Item item
            where item.itemId in :itemIds
            order by item.id asc
            """)
    List<Item> findAllByItemIdInForUpdateOrderByIdAsc(@Param("itemIds") List<String> itemIds);

    List<Item> findAllByStatusOrderByApprovedAtDesc(ItemStatus status);

    List<Item> findAllByOrderByApprovedAtDesc();
}
