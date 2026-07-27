package com.juliandonati.backendPortafolio.security.evaluator;

import com.juliandonati.backendPortafolio.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobSecurityEvaluator {
    private final JobRepository jobRepository;

    public boolean isOwner(Long jobId, String username){
        return jobRepository.IsJobByIdOwnedByUsername(jobId, username);
    }
}
