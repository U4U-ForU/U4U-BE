package com.ufu.domain.cauldron.service;

import com.ufu.domain.cauldron.domain.CauldronRecipeStatus;
import com.ufu.domain.cauldron.exception.CauldronRecipeDependencyCycleException;
import com.ufu.domain.cauldron.repository.CauldronRecipeDependencyProjection;
import com.ufu.domain.cauldron.repository.CauldronRecipeMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CauldronRecipeDependencyValidator {
    private final CauldronRecipeMaterialRepository cauldronRecipeMaterialRepository;

    public void validateNoCycle(Long resultItemId, Set<Long> materialItemIds) {
        Map<Long, Set<Long>> graph = buildGraph();
        graph.put(resultItemId, new HashSet<>(materialItemIds));

        if (hasCycle(resultItemId, graph, new HashMap<>())) {
            throw CauldronRecipeDependencyCycleException.EXCEPTION;
        }
    }

    private Map<Long, Set<Long>> buildGraph() {
        Map<Long, Set<Long>> graph = new HashMap<>();

        for (CauldronRecipeDependencyProjection dependency
                : cauldronRecipeMaterialRepository.findAllDependenciesByRecipeStatus(
                        CauldronRecipeStatus.ACTIVE
                )) {
            graph.computeIfAbsent(dependency.getResultItemId(), ignored -> new HashSet<>())
                    .add(dependency.getMaterialItemId());
        }

        return graph;
    }

    private boolean hasCycle(
            Long itemId,
            Map<Long, Set<Long>> graph,
            Map<Long, VisitState> visitStates
    ) {
        VisitState state = visitStates.getOrDefault(itemId, VisitState.UNVISITED);

        if (state == VisitState.VISITING) {
            return true;
        }

        if (state == VisitState.VISITED) {
            return false;
        }

        visitStates.put(itemId, VisitState.VISITING);

        for (Long materialItemId : graph.getOrDefault(itemId, Set.of())) {
            if (hasCycle(materialItemId, graph, visitStates)) {
                return true;
            }
        }

        visitStates.put(itemId, VisitState.VISITED);
        return false;
    }

    private enum VisitState {
        UNVISITED,
        VISITING,
        VISITED
    }
}
