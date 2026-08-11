package com.ufu.domain.item.repository;

import com.ufu.domain.item.domain.Item;
import com.ufu.domain.item.domain.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByItemId(String itemId);

    List<Item> findAllByStatusOrderByApprovedAtDesc(ItemStatus status);

    List<Item> findAllByOrderByApprovedAtDesc();
}
