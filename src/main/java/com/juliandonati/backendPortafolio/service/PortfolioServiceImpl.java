package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.*;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.repository.AboutMeRepository;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import com.juliandonati.backendPortafolio.repository.PresentationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService{
    private final PortfolioRepository portfolioRepository;
    private final AboutMeRepository aboutMeRepository;
    private final PresentationRepository presentationRepository;
    private final FileStorageService fileStorageService;
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
    @Transactional(readOnly = true)
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
        Portfolio portfolioToDelete = portfolioRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("No se encontró un portfolio con la id: " + id));

        portfolioToDelete.setSkills(null);
        portfolioToDelete.setExperience(null);
        portfolioToDelete.setDegrees(null);
        portfolioToDelete.setAboutMe(null);
        portfolioToDelete.setPresentation(null);
        portfolioRepository.save(portfolioToDelete);

        portfolioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAboutMeById(long aboutMeId) throws Exception {
        AboutMe aboutMe = aboutMeRepository.findById(aboutMeId).orElseThrow(()->new ResourceNotFoundException("No se encontró un AboutMe con la id: "+ aboutMeId));

        Portfolio portfolio = aboutMe.getPortfolio();


        if(portfolio != null){
            String aboutMeImgUrl = aboutMe.getBgImgUrl();
            if(aboutMeImgUrl != null && !aboutMeImgUrl.trim().isEmpty())
                fileStorageService.deleteImageByUrl(aboutMeImgUrl);

            portfolio.setAboutMe(null);
            aboutMe.setPortfolio(null);
            portfolioRepository.save(portfolio);
        }
    }

    @Override
    @Transactional
    public void deletePresentationById(long presentationId) throws Exception{
        Presentation presentation = presentationRepository.findById(presentationId).orElseThrow(()->new ResourceNotFoundException("No se encontró un Presentation con la id: "+ presentationId));

        Portfolio portfolio = presentation.getPortfolio();
        if(portfolio != null){
            String presentationImgUrl = presentation.getImgUrl();
            if(presentationImgUrl != null && !presentationImgUrl.trim().isEmpty())
                fileStorageService.deleteImageByUrl(presentationImgUrl);
            
            portfolio.setPresentation(null);
            presentation.setPortfolio(null);
            portfolioRepository.save(portfolio);
        }
    }

    @Override
    @Transactional
    public void deleteAllPortfolioImagesById(Long id) throws Exception {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No se encontró un portfolio con la id: " + id));

        List<String> imgUrlList = new ArrayList<>(portfolio.getSkills().stream().map(Skill::getImgUrl).toList());
        imgUrlList.add(portfolio.getPresentation().getImgUrl());
        imgUrlList.add(portfolio.getAboutMe().getBgImgUrl());
        imgUrlList.addAll(portfolio.getDegrees().stream().map(Degree::getImgUrl).toList());

        for(String imgUrl : imgUrlList){
            if(imgUrl != null && !imgUrl.trim().isEmpty())
                fileStorageService.deleteImageByUrl(imgUrl);
        }
    }
}
