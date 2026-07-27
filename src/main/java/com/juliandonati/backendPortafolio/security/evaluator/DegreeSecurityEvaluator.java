package com.juliandonati.backendPortafolio.security.evaluator;

import com.juliandonati.backendPortafolio.repository.DegreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DegreeSecurityEvaluator {
    private final DegreeRepository degreeRepository;

    public boolean isOwner(Long degreeId, String username){
        return degreeRepository.isDegreeByIdOwnedByUsername(degreeId, username);
    }
}
