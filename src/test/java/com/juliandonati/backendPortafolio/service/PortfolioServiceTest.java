package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.*;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

@Transactional
class PortfolioServiceTest {
    private final PortfolioService portfolioService;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final AboutMeService aboutMeService; // Para comprobar eliminación del AboutMe
    private final PresentationService presentationService; // Para comprobar eliminación del Presentation
    private final EntityManager entityManager;

    @Autowired
    public PortfolioServiceTest(PortfolioService portfolioService,
                                PortfolioRepository portfolioRepository,
                                UserRepository userRepository,
                                AboutMeService aboutMeService,
                                PresentationService presentationService,
                                EntityManager entityManager) {
        this.portfolioService = portfolioService;
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.aboutMeService = aboutMeService;
        this.presentationService = presentationService;
        this.entityManager = entityManager;
    }


    private final String aboutMeTitle = "AboutMe Test Title";
    private final String aboutMeDesc = "AboutMe Test Desc";
    private final String aboutMeImgUrl = "https://aboutmeimg.org";
    private final String aboutMeBtnText = "AboutMe Test Btn";
    private final String aboutMeBtnUrl = "https://aboutmeurl.org";
    private final String presentationName = "Presentation Test Name";
    private final String presentationTitle = "Presentation Test Title";
    private final String presentationDesc = "Presentation Test Desc";
    private final String presentationEmail = "presentation@test.com";
    private final String presentationPhoneNumber = "541234567890";
    private final String presentationImgUrl = "https:/presentationimg.net";

    @Test
    void testFindPortfolioByOwnerUsernameReturnsPortfolio() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.setAboutMe(new AboutMe(null, aboutMeTitle, aboutMeDesc, aboutMeImgUrl, aboutMeBtnText, aboutMeBtnUrl, null));
        portfolio.setPresentation(new Presentation(null, presentationName, presentationTitle, presentationDesc, presentationImgUrl, presentationEmail, presentationPhoneNumber, null));
        portfolio.addSkill(new Skill(null, "placeholder1", "placeholder", "placeholder", null, "placeholder", portfolio));
        portfolio.addSkill(new Skill(null, "placeholder2", "placeholder", "placeholder", null, "placeholder", portfolio));
        portfolio.addSkill(new Skill(null, "placeholder3", "placeholder", "placeholder", null, "placeholder", portfolio));
        portfolio.addDegree(new Degree(null, "placeholder", "placeholder", LocalDate.now(), null, null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder1", "placeholder", "placeholder", LocalDate.now(), null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder2", "placeholder", "placeholder", LocalDate.now(), null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder3", "placeholder", "placeholder", LocalDate.now(), null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder4", "placeholder", "placeholder", LocalDate.now(), null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder5", "placeholder", "placeholder", LocalDate.now(), null, portfolio));

        portfolioRepository.save(portfolio);

        // Act
        Portfolio result = portfolioService.findByOwnerUsername(TEST_OWNER_USERNAME);

        // Assert
        assertAll("Validando los campos del Portfolio...",
                () -> assertNotNull(result),
                () -> assertNotNull(result.getAboutMe()),
                () -> assertEquals(aboutMeTitle, result.getAboutMe().getTitle()),
                () -> assertEquals(aboutMeDesc, result.getAboutMe().getDescription()),
                () -> assertEquals(aboutMeImgUrl, result.getAboutMe().getBgImgUrl()),
                () -> assertEquals(aboutMeBtnText, result.getAboutMe().getButtonText()),
                () -> assertEquals(aboutMeBtnUrl, result.getAboutMe().getButtonUrl()),
                () -> assertNotNull(result.getPresentation()),
                () -> assertEquals(presentationName, result.getPresentation().getName()),
                () -> assertEquals(presentationTitle, result.getPresentation().getTitle()),
                () -> assertEquals(presentationDesc, result.getPresentation().getDescription()),
                () -> assertEquals(presentationEmail, result.getPresentation().getEmail()),
                () -> assertEquals(presentationPhoneNumber, result.getPresentation().getPhoneNumber()),
                () -> assertEquals(presentationImgUrl, result.getPresentation().getImgUrl()),
                () -> assertEquals(3, result.getSkills().size()),
                () -> assertEquals(1, result.getDegrees().size()),
                () -> assertEquals(5, result.getExperience().size())
        );
    }

    @Test
    void testPortfolioExistsByOwnerUsernameReturnsTrue() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolioRepository.save(portfolio);

        // Act
        boolean result = portfolioService.existsByOwnerUsername(TEST_OWNER_USERNAME);

        // Assert
        assertTrue(result);
    }

    @Test
    void testPortfolioExistsByOwnerUsernameReturnsFalse() {
        // Act
        boolean result = portfolioService.existsByOwnerUsername(TEST_OWNER_USERNAME);

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteAboutMeByIdDeletesAboutMeSuccessfully() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.setAboutMe(new AboutMe(null, aboutMeTitle, aboutMeDesc, aboutMeImgUrl, aboutMeBtnText, aboutMeBtnUrl, portfolio));
        Long aboutMeId = portfolioRepository.save(portfolio).getAboutMe().getId();

        // Act + Assert
        entityManager.clear();
        assertDoesNotThrow(() -> {
            portfolioService.deleteAboutMeById(aboutMeId);
            entityManager.flush();
        }, TEST_THROWS_MESSAGE);
        assertThrows(ResourceNotFoundException.class, () -> aboutMeService.findById(aboutMeId));
    }

    @Test
    void testDeletePresentationByIdDeletesPresentationSuccessfully() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.setPresentation(new Presentation(null, presentationName, presentationTitle, presentationDesc, presentationImgUrl, presentationEmail, presentationPhoneNumber, portfolio));
        Long presentationId = portfolioRepository.save(portfolio).getPresentation().getId();

        // Act + Assert
        entityManager.clear();
        assertDoesNotThrow(() -> {
            portfolioService.deletePresentationById(presentationId);
            entityManager.flush();
        }, TEST_THROWS_MESSAGE);
        assertThrows(ResourceNotFoundException.class, () -> presentationService.findById(presentationId));
    }

    @Test
    void testPortfolioCRUDLifeCycle() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.setAboutMe(new AboutMe(null, aboutMeTitle, aboutMeDesc, aboutMeImgUrl, aboutMeBtnText, aboutMeBtnUrl, null));
        portfolio.setPresentation(new Presentation(null, presentationName, presentationTitle, presentationDesc, presentationImgUrl, presentationEmail, presentationPhoneNumber, null));
        portfolio.addSkill(new Skill(null, "placeholder1", "placeholder", "placeholder", null, "placeholder", portfolio));
        portfolio.addSkill(new Skill(null, "placeholder2", "placeholder", "placeholder", null, "placeholder", portfolio));
        portfolio.addSkill(new Skill(null, "placeholder3", "placeholder", "placeholder", null, "placeholder", portfolio));
        portfolio.addDegree(new Degree(null, "placeholder", "placeholder", LocalDate.now(), null, null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder1", "placeholder", "placeholder", LocalDate.now(), null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder2", "placeholder", "placeholder", LocalDate.now(), null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder3", "placeholder", "placeholder", LocalDate.now(), null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder4", "placeholder", "placeholder", LocalDate.now(), null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder5", "placeholder", "placeholder", LocalDate.now(), null, portfolio));

        // CREATE
        Portfolio savedPortfolio = portfolioService.save(portfolio);
        Long portfolioId = savedPortfolio.getId();
        assertAll("Validando los campos del Portfolio...",
                () -> assertNotNull(savedPortfolio),
                () -> assertNotNull(portfolioId),
                () -> assertNotNull(savedPortfolio.getAboutMe()),
                () -> assertEquals(aboutMeTitle, savedPortfolio.getAboutMe().getTitle()),
                () -> assertEquals(aboutMeDesc, savedPortfolio.getAboutMe().getDescription()),
                () -> assertEquals(aboutMeImgUrl, savedPortfolio.getAboutMe().getBgImgUrl()),
                () -> assertEquals(aboutMeBtnText, savedPortfolio.getAboutMe().getButtonText()),
                () -> assertEquals(aboutMeBtnUrl, savedPortfolio.getAboutMe().getButtonUrl()),
                () -> assertNotNull(savedPortfolio.getPresentation()),
                () -> assertEquals(presentationName, savedPortfolio.getPresentation().getName()),
                () -> assertEquals(presentationTitle, savedPortfolio.getPresentation().getTitle()),
                () -> assertEquals(presentationDesc, savedPortfolio.getPresentation().getDescription()),
                () -> assertEquals(presentationEmail, savedPortfolio.getPresentation().getEmail()),
                () -> assertEquals(presentationPhoneNumber, savedPortfolio.getPresentation().getPhoneNumber()),
                () -> assertEquals(presentationImgUrl, savedPortfolio.getPresentation().getImgUrl()),
                () -> assertEquals(3, savedPortfolio.getSkills().size()),
                () -> assertEquals(1, savedPortfolio.getDegrees().size()),
                () -> assertEquals(5, savedPortfolio.getExperience().size())
        );


        // READ
        Portfolio searchedPortfolio = portfolioService.findById(portfolioId);

        assertAll("Validando los campos del Portfolio...",
                () -> assertNotNull(searchedPortfolio),
                () -> assertEquals(portfolioId, searchedPortfolio.getId()),
                () -> assertNotNull(searchedPortfolio.getAboutMe()),
                () -> assertEquals(aboutMeTitle, searchedPortfolio.getAboutMe().getTitle()),
                () -> assertEquals(aboutMeDesc, searchedPortfolio.getAboutMe().getDescription()),
                () -> assertEquals(aboutMeImgUrl, searchedPortfolio.getAboutMe().getBgImgUrl()),
                () -> assertEquals(aboutMeBtnText, searchedPortfolio.getAboutMe().getButtonText()),
                () -> assertEquals(aboutMeBtnUrl, searchedPortfolio.getAboutMe().getButtonUrl()),
                () -> assertNotNull(searchedPortfolio.getPresentation()),
                () -> assertEquals(presentationName, searchedPortfolio.getPresentation().getName()),
                () -> assertEquals(presentationTitle, searchedPortfolio.getPresentation().getTitle()),
                () -> assertEquals(presentationDesc, searchedPortfolio.getPresentation().getDescription()),
                () -> assertEquals(presentationEmail, searchedPortfolio.getPresentation().getEmail()),
                () -> assertEquals(presentationPhoneNumber, searchedPortfolio.getPresentation().getPhoneNumber()),
                () -> assertEquals(presentationImgUrl, searchedPortfolio.getPresentation().getImgUrl()),
                () -> assertEquals(3, searchedPortfolio.getSkills().size()),
                () -> assertEquals(1, searchedPortfolio.getDegrees().size()),
                () -> assertEquals(5, searchedPortfolio.getExperience().size())
        );

        // UPDATE
        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setOwner(
                userRepository.findByUsername(TEST_OWNER_USERNAME).orElseThrow(() -> new AssertionError("No se cargó correctamente el usuario de prueba"))
        );
        newPortfolio.setSkills(savedPortfolio.getSkills());
        newPortfolio.setExperience(savedPortfolio.getExperience());
        newPortfolio.setDegrees(savedPortfolio.getDegrees());

        String newAboutMeTitle = "New AboutMe Title!!!";
        String newAboutMeDesc = "New AboutMe Desc!!!";
        String newAboutMeImgUrl = "https://www.newaboutmeimgurl.com";
        String newAboutMeBtnText = "New AboutMe Btn Text!!!";
        String newAboutMeBtnUrl = "https://www.newaboutmebtnurl.com";
        newPortfolio.setAboutMe(new AboutMe(savedPortfolio.getAboutMe().getId(), newAboutMeTitle, newAboutMeDesc, newAboutMeImgUrl, newAboutMeBtnText, newAboutMeBtnUrl, newPortfolio));

        String newPresentationName = "New Presentation Name!!!";
        String newPresentationTitle = "New Presentation Title!!!";
        String newPresentationDesc = "New Presentation Desc!!!";
        String newPresentationEmail = "new.presentation@test.com";
        String newPresentationPhoneNumber = "541234567895";
        String newPresentationImgUrl = "https://newPresentationimg.net";
        newPortfolio.setPresentation(new Presentation(savedPortfolio.getPresentation().getId(), newPresentationName, newPresentationTitle, newPresentationDesc, newPresentationImgUrl, newPresentationEmail, newPresentationPhoneNumber, newPortfolio));

        portfolio.addSkill(new Skill(null, "placeholder4", "placeholder", "placeholder", null, "placeholder", null));
        portfolio.addDegree(new Degree(null, "placeholder2", "placeholder", LocalDate.now(), null, null, portfolio));
        portfolio.addExperience(new Job(null, "placeholder6", "placeholder", "placeholder", LocalDate.now(), null, portfolio));

        Portfolio updatedPortfolio = portfolioService.update(newPortfolio, portfolioId);


        assertAll("Validando los campos del Portfolio...",
                () -> assertNotNull(updatedPortfolio),
                () -> assertEquals(portfolioId, updatedPortfolio.getId()),
                () -> assertNotNull(updatedPortfolio.getAboutMe()),
                () -> assertEquals(newAboutMeTitle, updatedPortfolio.getAboutMe().getTitle()),
                () -> assertEquals(newAboutMeDesc, updatedPortfolio.getAboutMe().getDescription()),
                () -> assertEquals(newAboutMeImgUrl, updatedPortfolio.getAboutMe().getBgImgUrl()),
                () -> assertEquals(newAboutMeBtnText, updatedPortfolio.getAboutMe().getButtonText()),
                () -> assertEquals(newAboutMeBtnUrl, updatedPortfolio.getAboutMe().getButtonUrl()),
                () -> assertNotNull(updatedPortfolio.getPresentation()),
                () -> assertEquals(newPresentationName, updatedPortfolio.getPresentation().getName()),
                () -> assertEquals(newPresentationTitle, updatedPortfolio.getPresentation().getTitle()),
                () -> assertEquals(newPresentationDesc, updatedPortfolio.getPresentation().getDescription()),
                () -> assertEquals(newPresentationEmail, updatedPortfolio.getPresentation().getEmail()),
                () -> assertEquals(newPresentationPhoneNumber, updatedPortfolio.getPresentation().getPhoneNumber()),
                () -> assertEquals(newPresentationImgUrl, updatedPortfolio.getPresentation().getImgUrl()),
                () -> assertEquals(4, updatedPortfolio.getSkills().size()),
                () -> assertEquals(2, updatedPortfolio.getDegrees().size()),
                () -> assertEquals(6, updatedPortfolio.getExperience().size())
        );

        // DELETE
        entityManager.clear();
        assertDoesNotThrow(() -> {
            portfolioService.deleteById(portfolioId);
            entityManager.flush();
        }, TEST_THROWS_MESSAGE);
        assertThrows(ResourceNotFoundException.class, () -> portfolioService.findById(portfolioId));
    }
}