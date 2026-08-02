package com.juliandonati.backendPortafolio.security.evaluator;

import com.juliandonati.backendPortafolio.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectSecurityEvaluator {
    private final ProjectRepository projectRepository;

    public boolean isOwner(Long id, String ownerUsername){
        return projectRepository.isProjectByIdOwnedByUsername(id,ownerUsername);
    }
}
