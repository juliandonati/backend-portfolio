package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.domain.Presentation;
import com.juliandonati.backendPortafolio.dto.PresentationDto;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.createAndSaveUser;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PresentationServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private PresentationService presentationService;

    private final String ownerUsername = MiscTestUtilities.TEST_OWNER_USERNAME;

    private final String presentationName = "nombre pres.";
    private final String presentationTitle = "titulo pres.";
    private final String presentationDesc = "desc. pres.";
    private final String presentationImgUrl = "http://ejemplo.com";
    private final String presentationEmail = "contactemail@example.com";
    private final String presentationPhone = "541234567890";

    private Portfolio createPortfolioWithPresentation(){
        User user = createAndSaveUser(userRepository);
        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(user);
        Presentation presentation = new Presentation(null,presentationName,presentationTitle,presentationDesc,presentationImgUrl,presentationEmail,presentationPhone,null);
        portfolio.setPresentation(presentation);
        return portfolio;
    }

    @Test
    void testFindPresentationByOwnerUsernameReturnsPresentation() {
        // Arrange
        Portfolio portfolio = createPortfolioWithPresentation();
        portfolioRepository.save(portfolio);

        // Act
        PresentationDto result = presentationService.findByOwnerUsername(ownerUsername);

        // Assert
        assertAll("Validando campos del PresentationDto...",
                () -> assertNotNull(result),
                () -> assertEquals(presentationName, result.getName()),
                () -> assertEquals(presentationTitle, result.getTitle()),
                () -> assertEquals(presentationDesc, result.getDescription()),
                () -> assertEquals(presentationImgUrl, result.getImgUrl()),
                () -> assertEquals(presentationEmail, result.getEmail()),
                () -> assertEquals(presentationPhone, result.getPhoneNumber())
        );
    }

    @Test
    void testFindPresentationImgUrlByOwnerUsernameReturnsImgUrl() {
        // Arrange
        Portfolio portfolio = createPortfolioWithPresentation();
        portfolioRepository.save(portfolio);

        // Act
        String result = presentationService.findImgUrlByOwnerUsername(ownerUsername);

        // Assert
        assertNotNull(result);
        assertEquals(presentationImgUrl,result);
    }

    @Test
    void testPresentationExistsByOwnerUsernameReturnsTrue() {
        // Arrange
        Portfolio portfolio = createPortfolioWithPresentation();
        portfolioRepository.save(portfolio);

        // Act
        boolean result = presentationService.existsByOwnerUsername(ownerUsername);

        // Assert
        assertTrue(result);
    }

    @Test
    void testPresentationExistsByOwnerUsernameReturnsFalse() {
        // Act
        boolean result = presentationService.existsByOwnerUsername(ownerUsername);

        // Assert
        assertFalse(result);
    }

    @Test
    void testPresentationCRUDLifeCycle(){
        // Arrange
        Portfolio portfolio = createPortfolioWithPresentation();
        // CREATE

        Presentation savedPresentation = portfolioRepository.save(portfolio).getPresentation();
        Long presentationId = savedPresentation.getId();
        assertAll("Validando campos del PresentationDto...",
                () -> assertNotNull(savedPresentation),
                () -> assertNotNull(presentationId),
                () -> assertEquals(presentationName, savedPresentation.getName()),
                () -> assertEquals(presentationTitle, savedPresentation.getTitle()),
                () -> assertEquals(presentationDesc, savedPresentation.getDescription()),
                () -> assertEquals(presentationImgUrl, savedPresentation.getImgUrl()),
                () -> assertEquals(presentationEmail, savedPresentation.getEmail()),
                () -> assertEquals(presentationPhone, savedPresentation.getPhoneNumber())
        );

        // READ
        PresentationDto searchedPresentationDto = presentationService.findById(presentationId);

        assertAll("Validando campos del PresentationDto...",
                () -> assertNotNull(searchedPresentationDto),
                () -> assertEquals(presentationId,searchedPresentationDto.getId()),
                () -> assertEquals(presentationName, searchedPresentationDto.getName()),
                () -> assertEquals(presentationTitle, searchedPresentationDto.getTitle()),
                () -> assertEquals(presentationDesc, searchedPresentationDto.getDescription()),
                () -> assertEquals(presentationImgUrl, searchedPresentationDto.getImgUrl()),
                () -> assertEquals(presentationEmail, searchedPresentationDto.getEmail()),
                () -> assertEquals(presentationPhone, searchedPresentationDto.getPhoneNumber())
        );

        // UPDATE
        String newPresentationName = "nombre pres nuevo.";
        String newPresentationTitle = "titulo pres nuevo.";
        String newPresentationDesc = "desc. pres nuevo.";
        String newPresentationImgUrl = "http://ejemplonuevo.net";
        String newPresentationEmail = "contactemailnuevo@example.org";
        String newPresentationPhone = "541234567891";
        PresentationDto newPresentationDto = new PresentationDto(null,newPresentationName,newPresentationTitle,newPresentationDesc,newPresentationImgUrl,newPresentationEmail,newPresentationPhone);

        PresentationDto updatedPresentationDto = presentationService.update(newPresentationDto,presentationId);

        assertAll("Validando campos del PresentationDto...",
                () -> assertNotNull(updatedPresentationDto),
                () -> assertEquals(presentationId,updatedPresentationDto.getId()),
                () -> assertEquals(newPresentationName, updatedPresentationDto.getName()),
                () -> assertEquals(newPresentationTitle, updatedPresentationDto.getTitle()),
                () -> assertEquals(newPresentationDesc, updatedPresentationDto.getDescription()),
                () -> assertEquals(newPresentationImgUrl, updatedPresentationDto.getImgUrl()),
                () -> assertEquals(newPresentationEmail, updatedPresentationDto.getEmail()),
                () -> assertEquals(newPresentationPhone, updatedPresentationDto.getPhoneNumber())
        );

        // DELETE -> Se testea en PortfolioServiceTest, el método le pertenece a PortfolioService
    }
}