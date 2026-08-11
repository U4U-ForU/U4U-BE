package com.ufu.domain.cauldron.repository;

import com.ufu.domain.cauldron.domain.CauldronRecipe;
import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CauldronRecipeRepository extends JpaRepository<CauldronRecipe, Long> {
    @Query("""
            select recipe
            from CauldronRecipe recipe
            join fetch recipe.resultItem
            where recipe.status = :status
            order by recipe.createdAt desc
            """)
    List<CauldronRecipe> findAllWithResultItemByStatus(@Param("status") CauldronRecipeStatus status); //

    @Query("""
            select recipe
            from CauldronRecipe recipe
            join fetch recipe.resultItem
            where recipe.recipeId = :recipeId
                and recipe.status = :status
            """)
    Optional<CauldronRecipe> findWithResultItemByRecipeIdAndStatus(
            @Param("recipeId") String recipeId,
            @Param("status") CauldronRecipeStatus status
    );
}
