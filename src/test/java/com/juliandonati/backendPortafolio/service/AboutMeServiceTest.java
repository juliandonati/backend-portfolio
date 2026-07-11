package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.AboutMe;
import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.dto.AboutMeDto;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AboutMeServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private AboutMeService aboutMeService;

    private final String ownerUsername = MiscTestUtilities.TEST_OWNER_USERNAME;

    private final String title = "About Me";
    private final String desc = "Love to code solutions";
    private final String imgUrl = "http://www,imgurl.test";
    private final String btnText = "Click here!";
    private final String btnUrl = "https://www.github.com";


    Portfolio createPortfolioWithAboutMe(){
        User user = new User(null, ownerUsername, "password", "displayName", "user@example.com", Set.of(), null, Set.of());
        userService.save(user);
        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(user);
        AboutMe aboutMe = new AboutMe(null, title, desc, imgUrl, btnText, btnUrl, portfolio);
        portfolio.setAboutMe(aboutMe);
        return portfolio;
    }

    @Test
    void testAboutMeCRUDLifeCycle() {
        // Arrange
        Portfolio portfolio = createPortfolioWithAboutMe();

        // CREATE
        AboutMe savedAboutMe = portfolioService.save(portfolio).getAboutMe();
        Long id = savedAboutMe.getId();

        assertAll("Validando los campos del About-Me...",
                () -> assertNotNull(savedAboutMe),
                () -> assertNotNull(id),
                () -> assertEquals(title, savedAboutMe.getTitle()),
                () -> assertEquals(desc, savedAboutMe.getDescription()),
                () -> assertEquals(imgUrl, savedAboutMe.getBgImgUrl()),
                () -> assertEquals(btnText, savedAboutMe.getButtonText()),
                () -> assertEquals(btnUrl, savedAboutMe.getButtonUrl())
        );
        // READ
        AboutMeDto searchedAboutMeDto = aboutMeService.findById(id);

        assertAll("Validando los campos del About-Me...",
                () -> assertNotNull(searchedAboutMeDto),
                () -> assertEquals(id, searchedAboutMeDto.getId()),
                () -> assertEquals(title, searchedAboutMeDto.getTitle()),
                () -> assertEquals(desc, searchedAboutMeDto.getDescription()),
                () -> assertEquals(imgUrl, searchedAboutMeDto.getBgImgUrl()),
                () -> assertEquals(btnText, searchedAboutMeDto.getButtonText()),
                () -> assertEquals(btnUrl, searchedAboutMeDto.getButtonUrl())
        );

        // UPDATE
        String newTitle = "New About Me", newDesc = "Like to code solutions", newImgUrl = "http://www,imgurl.com", newBtnText = "Click me!", newBtnUrl = "https://www.google.com";
        AboutMeDto newAboutMeDto = new AboutMeDto(null, newTitle, newDesc, newImgUrl, newBtnText, newBtnUrl);

        AboutMeDto updateResult = aboutMeService.update(newAboutMeDto, id);

        assertAll("Validando los campos del About-Me...",
                () -> assertNotNull(updateResult),
                () -> assertEquals(id, updateResult.getId()),
                () -> assertEquals(newTitle, updateResult.getTitle()),
                () -> assertEquals(newDesc, updateResult.getDescription()),
                () -> assertEquals(newImgUrl, updateResult.getBgImgUrl()),
                () -> assertEquals(newBtnText, updateResult.getButtonText()),
                () -> assertEquals(newBtnUrl, updateResult.getButtonUrl())
        );

        // DELETE NO, PORQUE SE REALIZA EN PortfolioService
    }

    @Test
    void testFindAboutMeByOwnerUsernameReturnsAboutMe() {
        // Arrange
        Portfolio portfolio = createPortfolioWithAboutMe();
        portfolioService.save(portfolio);

        // Act
        AboutMeDto result = aboutMeService.findByOwnerUsername(ownerUsername);

        // Assert
        assertNotNull(result);
        assertEquals(title,result.getTitle());
        assertEquals(desc,result.getDescription());
        assertEquals(imgUrl,result.getBgImgUrl());
        assertEquals(btnText,result.getButtonText());
        assertEquals(btnUrl,result.getButtonUrl());
    }

    @Test
    void testAboutMeExistsByOwnerUsernameReturnsTrue() {
        // Arrange
        Portfolio portfolio = createPortfolioWithAboutMe();
        portfolioService.save(portfolio);

        // Act
        boolean result = aboutMeService.existsByOwnerUsername(ownerUsername);

        // Assert
        assertTrue(result);
    }

    @Test
    void testAboutMeExistsByOwnerUsernameReturnsFalse() {
        // Act
        boolean result = aboutMeService.existsByOwnerUsername("usuarioinexistente");

        // Assert
        assertFalse(result);
    }
}