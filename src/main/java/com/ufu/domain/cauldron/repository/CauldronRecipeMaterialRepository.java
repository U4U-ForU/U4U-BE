package com.ufu.domain.cauldron.repository;

import com.ufu.domain.cauldron.domain.CauldronRecipeMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CauldronRecipeMaterialRepository extends JpaRepository<CauldronRecipeMaterial, Long> {
    @Query("""
            select material
            from CauldronRecipeMaterial material
            join fetch material.item
            where material.cauldronRecipe.id = :cauldronRecipeId
            order by material.slotNumber asc
            """)
    List<CauldronRecipeMaterial> findAllWithItemByCauldronRecipeIdOrderBySlotNumberAsc(
            @Param("cauldronRecipeId") Long cauldronRecipeId
    );

    @Query("""
            select material
            from CauldronRecipeMaterial material
            join fetch material.item
            where material.cauldronRecipe.id in :cauldronRecipeIds
            order by material.cauldronRecipe.id asc, material.slotNumber asc
            """)
    List<CauldronRecipeMaterial> findAllWithItemByCauldronRecipeIdInOrderByCauldronRecipeIdAscSlotNumberAsc(
            @Param("cauldronRecipeIds") List<Long> cauldronRecipeIds
    ); //
}
