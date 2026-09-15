package com.ufu.domain.cauldron.domain;

import com.ufu.domain.item.domain.Item;
import com.ufu.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cauldron_recipe_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CauldronRecipe extends BaseEntity {
    public static final int MATERIAL_COUNT = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cauldron_recipe_id", nullable = false)
    private Long id;

    @Column(name = "recipe_id", nullable = false, unique = true, length = 36)
    private String recipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_item_id", nullable = false)
    private Item resultItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CauldronRecipeStatus status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private CauldronRecipe(Item resultItem) {
        this.recipeId = UUID.randomUUID().toString();
        this.resultItem = resultItem;
        this.status = CauldronRecipeStatus.ACTIVE;
    }

    public void delete(LocalDateTime deletedAt) {
        this.status = CauldronRecipeStatus.DELETED;
        this.deletedAt = deletedAt;
    }
}
