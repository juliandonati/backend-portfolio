package com.juliandonati.backendPortafolio.security.service;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.dto.UserSummaryResponseDto;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.TEST_THROWS_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

@Transactional
class UserServiceTest {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final EntityManager entityManager;
    @Autowired
    public UserServiceTest(UserService userService,
                           UserRepository userRepository,
                           PortfolioRepository portfolioRepository,
                           EntityManager entityManager){
        this.userService = userService;
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.entityManager = entityManager;
    }

    // PONER NOMBRES DISTINTOS A username1 y username2, QUE NINGUNO INCLUYA AL OTRO, PARA COMPROBAR QUE EL FILTRADO FUNCIONE

    private final String username1 = "usuariouno";
    private final String password1 = "contrauno";
    private final String dname1 = "nombreuno";
    private final String email1 = "emailuno@test.com";

    private final String username2 = "usuario2dos";
    private final String password2 = "contrados2";
    private final String dname2 = "nombre2";
    private final String email2 = "e.mail.dos@hotmail.net";


    @Test
    void testFindAllReturnsMatchingUsersSuccessfully() {
        User user1 = new User(null, username1, password1, dname1, email1, Set.of(), null, Set.of());
        User user2 = new User(null, username2, password2, dname2, email2, Set.of(), null, Set.of());
        userRepository.save(user1);
        userRepository.save(user2);

        Page<UserSummaryResponseDto> resultPageIncludingAll = userService.findAll("", PageRequest.of(0, 10));
        Page<UserSummaryResponseDto> resultPageOnlyTest1 = userService.findAll(username1, PageRequest.of(0, 10));
        Page<UserSummaryResponseDto> resultPageOnlyTest2 = userService.findAll(username2, PageRequest.of(0, 10));

        assertAll("Validando los resultados de las búsquedas...",
                // Solo buscar test 1
                () -> assertEquals(1, resultPageOnlyTest1.getTotalElements()),
                () -> assertNotNull(resultPageOnlyTest1.getContent().getFirst()),
                () -> assertEquals(username1, resultPageOnlyTest1.getContent().getFirst().getUsername()),

                // Solo buscar test 2
                () -> assertEquals(1, resultPageOnlyTest2.getTotalElements()),
                () -> assertNotNull(resultPageOnlyTest2.getContent().getFirst()),
                () -> assertEquals(username2, resultPageOnlyTest2.getContent().getFirst().getUsername()),

                // Buscar sin filtros
                () -> assertEquals(2, resultPageIncludingAll.getTotalElements()),
                () -> assertTrue(resultPageIncludingAll.getContent().stream().anyMatch(u -> u.getUsername().equals(username1))),
                () -> assertTrue(resultPageIncludingAll.getContent().stream().anyMatch(u -> u.getUsername().equals(username2)))
        );
    }

    @Test
    void testFindByEmailReturnsUserSuccessfully() {
        User user = new User(null, username1, password1, dname1, email1, Set.of(), null, Set.of());
        userRepository.save(user);

        User result = userService.findByEmail(email1);

        assertAll("Validando los campos del User...",
                () -> assertNotNull(result),
                () -> assertNotNull(result.getUsername()),
                () -> assertEquals(username1, result.getUsername()),
                () -> assertEquals(password1, result.getPassword()),
                () -> assertEquals(dname1, result.getDisplayName()),
                () -> assertEquals(email1, result.getEmail())
        );
    }

    @Test
    void testFindByUsernameReturnsUserSuccessfully() {
        User user = new User(null, username2, password2, dname2, email2, Set.of(), null, Set.of());
        userRepository.save(user);

        User result = userService.findByUsername(username2);

        assertAll("Validando los campos del User...",
                () -> assertNotNull(result),
                () -> assertEquals(username2, result.getUsername()),
                () -> assertEquals(password2, result.getPassword()),
                () -> assertEquals(dname2, result.getDisplayName()),
                () -> assertEquals(email2, result.getEmail())
        );
    }

    @Test
    void testRegisterRegistersUserSuccessfully() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername(username2);
        registerRequestDto.setUnencryptedPassword(password2);
        registerRequestDto.setDisplayName(dname2);
        registerRequestDto.setEmail(email2);

        User result = userService.register(registerRequestDto);

        assertAll("Validando los campos del User...",
                () -> assertNotNull(result),
                () -> assertEquals(username2, result.getUsername()),
                () -> assertNotEquals(password2, result.getPassword()),
                () -> assertEquals(dname2, result.getDisplayName()),
                () -> assertEquals(email2, result.getEmail())
        );
    }

    @Test
    void testDeleteByEmailDeletesUserSuccessfully() {
        User user = new User(null, username1, password1, dname1, email1, Set.of(), null, Set.of());
        userRepository.save(user);

        entityManager.clear();
        assertDoesNotThrow(() -> {
            userService.deleteByEmail(email1);
            entityManager.flush();
        }, TEST_THROWS_MESSAGE);
        assertTrue(userRepository.findByEmail(email1).isEmpty());
    }

    @Test
    void testDeleteByUsernameDeletesUserSuccessfully() {
        User user = new User(null, username1, password1, dname1, email1, Set.of(), null, Set.of());
        userRepository.save(user);

        entityManager.clear();
        assertDoesNotThrow(() -> {
            userService.deleteByUsername(username1);
            entityManager.flush();
        }, TEST_THROWS_MESSAGE);
        assertTrue(userRepository.findByUsername(username1).isEmpty());
    }

    @Test
    void testUserHasPortfolioReturnsTrue() {
        User user = new User(null, username2, password2, dname2, email2, Set.of(), null, Set.of());
        userRepository.save(user);

        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(user);
        portfolioRepository.save(portfolio);

        boolean result = userService.hasPortfolio(username2);

        assertTrue(result);
    }

    @Test
    void testUserHasPortfolioReturnsFalse() {
        User user = new User(null, username2, password2, dname2, email2, Set.of(), null, Set.of());
        userRepository.save(user);

        boolean result = userService.hasPortfolio(username2);

        assertFalse(result);
    }

    @Test
    void testUserCRUDLifeCycle() {
        // Arrange
        User userToSave = new User(null, username1, password1, dname1, email1, Set.of(), null, Set.of());

        // CREATE
        User savedUser = userService.save(userToSave);
        long userId = savedUser.getId();

        assertAll("Validando los campos del User guardado...",
                () -> assertNotNull(userId),
                () -> assertEquals(username1, savedUser.getUsername()),
                () -> assertEquals(password1, savedUser.getPassword()), // password1 sería la contraseña ya encriptada en este caso
                () -> assertEquals(dname1, savedUser.getDisplayName()),
                () -> assertEquals(email1, savedUser.getEmail())
        );
        // READ
        User searchedUser = userService.findById(userId);

        assertAll("Validando los campos del User guardado...",
                () -> assertEquals(userId, savedUser.getId()),
                () -> assertEquals(username1, searchedUser.getUsername()),
                () -> assertEquals(password1, searchedUser.getPassword()), // password1 sería la contraseña ya encriptada en este caso
                () -> assertEquals(dname1, searchedUser.getDisplayName()),
                () -> assertEquals(email1, searchedUser.getEmail())
        );
        // UPDATE
        // todo Implementar UPDATE de User

        // DELETE
        entityManager.clear();
        assertDoesNotThrow(() -> {
            userService.deleteById(userId);
            entityManager.flush();
        }, TEST_THROWS_MESSAGE);
        assertFalse(userRepository.existsById(userId));
    }
}