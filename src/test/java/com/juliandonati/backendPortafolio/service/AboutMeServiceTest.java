package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.AboutMe;
import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.dto.AboutMeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AboutMeServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private AboutMeService aboutMeService;

    @Test
    void testAboutMeCRUDLifeCycle() {
        // Arrange
        String ownerUsername = "usuarioTest";
        User user = new User(null, ownerUsername, "password", "displayName", "user@example.com", Set.of(), null, Set.of());
        userService.save(user);

        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(user);

        String title = "About Me", desc = "Love to code solutions", imgUrl = "http://www,imgurl.test", btnText = "Click here!", btnUrl = "https://www.github.com";
        AboutMe aboutMe = new AboutMe(null, title, desc, imgUrl, btnText, btnUrl, portfolio);
        portfolio.setAboutMe(aboutMe);

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
    }

    @Test
    void testAboutMeExistsByOwnerUsernameReturnsTrue() {
    }

    @Test
    void testAboutMeExistsByOwnerUsernameReturnsFalse() {
    }

    @Test
    void testDeleteAboutMeByOwnerUsernameDeletesAboutMeSuccessfully() {
    }
}