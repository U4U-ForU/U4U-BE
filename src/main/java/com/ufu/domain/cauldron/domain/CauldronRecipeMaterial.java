package com.ufu.domain.cauldron.domain;

import com.ufu.domain.item.domain.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "cauldron_recipe_material_tbl",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cauldron_recipe_id", "slot_number"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CauldronRecipeMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cauldron_recipe_material_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cauldron_recipe_id", nullable = false)
    private CauldronRecipe cauldronRecipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "slot_number", nullable = false)
    private int slotNumber;

    @Builder
    private CauldronRecipeMaterial(CauldronRecipe cauldronRecipe, Item item, int slotNumber) {
        this.cauldronRecipe = cauldronRecipe;
        this.item = item;
        this.slotNumber = slotNumber;
    }

    public void changeItem(Item item) {
        this.item = item;
    }
}
