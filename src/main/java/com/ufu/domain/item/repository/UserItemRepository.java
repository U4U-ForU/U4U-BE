package com.ufu.domain.item.repository;

import com.ufu.domain.item.domain.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    Optional<UserItem> findByUserIdAndItemId(Long userId, Long itemId);

    @Query("""
            select userItem
            from UserItem userItem
            join fetch userItem.item item
            join fetch item.creator
            where userItem.user.id = :userId
              and (:keyword is null or lower(item.name) like lower(concat('%', :keyword, '%')))
            """)
    List<UserItem> findAllByUserIdAndKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );

    @Query("""
            select userItem
            from UserItem userItem
            join fetch userItem.item item
            join fetch item.creator
            where userItem.user.id = :userId
              and item.itemId = :itemId
            """)
    Optional<UserItem> findByUserIdAndItemItemId(
            @Param("userId") Long userId,
            @Param("itemId") String itemId
    );
}
