package com.ufu.domain.item.repository;

import com.ufu.domain.item.domain.UserItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    Optional<UserItem> findByUserIdAndItemId(Long userId, Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select userItem
            from UserItem userItem
            join fetch userItem.item item
            where userItem.user.id = :userId
              and item.itemId = :itemId
            """)
    Optional<UserItem> findByUserIdAndItemItemIdForUpdate(
            @Param("userId") Long userId,
            @Param("itemId") String itemId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select userItem
            from UserItem userItem
            where userItem.user.id = :userId
              and userItem.item.id = :itemId
            """)
    Optional<UserItem> findByUserIdAndItemIdForUpdate(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId
    );

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
