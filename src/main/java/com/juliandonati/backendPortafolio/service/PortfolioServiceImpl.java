package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.AboutMe;
import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.domain.Presentation;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.repository.AboutMeRepository;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import com.juliandonati.backendPortafolio.repository.PresentationRepository;
import com.juliandonati.backendPortafolio.security.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService{
    private final PortfolioRepository portfolioRepository;
    private final AboutMeRepository aboutMeRepository;
    private final PresentationRepository presentationRepository;
    @Override
    @Transactional(readOnly = true)
    public Portfolio findById(long id) {
        return portfolioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró un portfolio con la id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Portfolio findByOwnerUsername(String username) {
        return portfolioRepository.findByOwnerUsername(username).orElseThrow(() -> new ResourceNotFoundException("No se encontró el portfolio de " + username));
    }

    @Override
    public boolean existsByOwnerUsername(String username) {
        return portfolioRepository.existsByOwnerUsername(username);
    }


    @Override
    @Transactional
    public Portfolio save(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public Portfolio update(Portfolio portfolio, Long id) {
        Portfolio portfolioToUpdate = portfolioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró un portfolio con la id: " + id));

        /* todo Cambiar todo esto por un simple mapper. No se puede testear como está ahora. */
        portfolioToUpdate.setOwner(portfolio.getOwner());
        portfolioToUpdate.setDegrees(portfolio.getDegrees());
        portfolioToUpdate.setExperience(portfolio.getExperience());
        portfolioToUpdate.setSkills(portfolio.getSkills());
        portfolioToUpdate.setPresentation(portfolio.getPresentation());
        portfolioToUpdate.setAboutMe(portfolio.getAboutMe());
        portfolioToUpdate.setAuthorizedUsers(portfolio.getAuthorizedUsers());

        return portfolioRepository.save(portfolioToUpdate);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        if(!portfolioRepository.existsById(id))
            throw new ResourceNotFoundException("No se encontró un portfolio con la id: " + id);
        portfolioRepository.deleteById(id);
    }

    @Override
    public void deleteAboutMeById(long aboutMeId) {
        AboutMe aboutMe = aboutMeRepository.findById(aboutMeId).orElseThrow(()->new ResourceNotFoundException("No se encontró un AboutMe con la id: "+ aboutMeId));

        Portfolio portfolio = aboutMe.getPortfolio();
        if(portfolio != null){
            portfolio.setAboutMe(null);
            aboutMe.setPortfolio(null);
            portfolioRepository.save(portfolio);
        }
    }

    @Override
    public void deletePresentationById(long presentationId) {
        Presentation presentation = presentationRepository.findById(presentationId).orElseThrow(()->new ResourceNotFoundException("No se encontró un Presentation con la id: "+ presentationId));

        Portfolio portfolio = presentation.getPortfolio();
        if(portfolio != null){
            portfolio.setAboutMe(null);
            presentation.setPortfolio(null);
            portfolioRepository.save(portfolio);
        }
    }
}
