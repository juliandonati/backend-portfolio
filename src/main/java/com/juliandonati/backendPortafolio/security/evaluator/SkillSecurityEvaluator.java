package com.juliandonati.backendPortafolio.security.evaluator;

import com.juliandonati.backendPortafolio.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkillSecurityEvaluator {
    private final SkillRepository skillRepository;

    public boolean isOwner(Long skillId, String username){
        return skillRepository.isSkillByIdOwnedByUsername(skillId, username);
    }
}
