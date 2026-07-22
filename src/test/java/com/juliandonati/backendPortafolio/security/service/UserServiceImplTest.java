package com.juliandonati.backendPortafolio.security.service;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.dto.UserSummaryResponseDto;
import com.juliandonati.backendPortafolio.security.exception.UserAlreadyExistsException;
import com.juliandonati.backendPortafolio.security.mapper.UserMapper;
import com.juliandonati.backendPortafolio.security.mapper.UserMapperImpl;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;


    @Mock
    PasswordEncoder passwordEncoder;

    private UserMapper userMapperSpy;

    private UserServiceImpl userService; // Lo inicializo en un BeforeEach, ya que tengo que inyectarle dependencias.

    long id1 = 1L;
    private final String username1 = "usuariouno";
    private final String password1 = "contrauno";
    private final String dname1 = "nombreuno";
    private final String email1 = "emailuno@test.com";

    Long id2 = 2L;
    private final String username2 = "usuario2dos";
    private final String password2 = "contrados2";
    private final String dname2 = "nombre2";
    private final String email2 = "e.mail.dos@hotmail.net";


    @BeforeEach
    void setup(){
        UserMapperImpl userMapperImpl = new UserMapperImpl();
        ReflectionTestUtils.setField(userMapperImpl,"passwordEncoder",passwordEncoder);
        userMapperSpy = spy(userMapperImpl);
        userService = new UserServiceImpl(userRepository,userMapperSpy);
    }

    @Test
    void testFindAllUsersReturnsPageOfUsers() {
        // Arrange
        User mockUser1 = new User(null, username1, password1, dname1, email1, Set.of(), null, Set.of());
        User mockUser2 = new User(null, username2, password2, dname2, email2, Set.of(), null, Set.of());

        List<User> mockUsers = List.of(mockUser1, mockUser2);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(mockUsers, pageable, mockUsers.size()));

        // Act
        Page<UserSummaryResponseDto> resultsPage = userService.findAll("", pageable);
        List<UserSummaryResponseDto> results = resultsPage.getContent();
        UserSummaryResponseDto result1 = results.getFirst();
        UserSummaryResponseDto result2 = results.getLast();

        // Como es un resultado mockeado, sé los índices de los resultados

        // Assert
        assertAll("Validando los campos de los UserSummaryResponseDto",
                () -> assertEquals(2, resultsPage.getTotalElements()),
                () -> assertEquals(1, resultsPage.getTotalPages()),
                () -> assertEquals(pageable, resultsPage.getPageable()),
                () -> assertNotNull(result1),
                () -> assertEquals(username1, result1.getUsername()),
                () -> assertEquals(dname1, result1.getDisplayName()),
                () -> assertNotNull(result2),
                () -> assertEquals(username2, result2.getUsername()),
                () -> assertEquals(dname2, result2.getDisplayName())
        );
        verify(userRepository,times(1)).findAll(pageable);
        verify(userMapperSpy,times(2)).toUserSummaryResponseDto(any(User.class));
    }

    @Test
    void testFindUserByIdReturnsUserSuccessfully() {
        // Arrange
        User mockUser = new User(id1, username1, password1, dname1, email1, Set.of(), null, Set.of());

        when(userRepository.findById(id1)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.findById(id1);

        // Assert
        assertAll("Validando los campos del User",
                () -> assertNotNull(result),
                () -> assertEquals(id1, result.getId()),
                () -> assertEquals(username1, result.getUsername()),
                () -> assertEquals(password1, result.getPassword()), // (Es la contraseña E N C R I P T A D A)
                () -> assertEquals(dname1, result.getDisplayName()),
                () -> assertEquals(email1, result.getEmail())
        );
        verify(userRepository,times(1)).findById(id1);
    }

    @Test
    void testFindUserByIdThrowsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->userService.findById(99L));
        verify(userRepository,times(1)).findById(99L);
    }

    @Test
    void testFindUserByEmailReturnsUserSuccessfully() {
        // Arrange
        User mockUser = new User(id1, username1, password1, dname1, email1, Set.of(), null, Set.of());

        when(userRepository.findByEmail(email1)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.findByEmail(email1);

        // Assert
        assertAll("Validando los campos del User",
                () -> assertNotNull(result),
                () -> assertEquals(id1, result.getId()),
                () -> assertEquals(username1, result.getUsername()),
                () -> assertEquals(password1, result.getPassword()), // (Es la contraseña E N C R I P T A D A)
                () -> assertEquals(dname1, result.getDisplayName()),
                () -> assertEquals(email1, result.getEmail())
        );
        verify(userRepository,times(1)).findByEmail(email1);
    }

    @Test
    void testFindUserByEmailThrowsResourceNotFoundException() {
        String unexistentEmail = "unexistent@email.com";
        when(userRepository.findByEmail(unexistentEmail)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->userService.findByEmail(unexistentEmail));
        verify(userRepository,times(1)).findByEmail(unexistentEmail);
    }

    @Test
    void testFindUserByUsernameReturnsUserSuccessfully() {
        User mockUser = new User(id2,username2,password2,dname2,email2,Set.of(),null,Set.of());
        when(userRepository.findByUsername(username2)).thenReturn(Optional.of(mockUser));

        User result = userService.findByUsername(username2);

        assertAll("Validando los campos del User",
                () -> assertNotNull(result),
                () -> assertEquals(id2, result.getId()),
                () -> assertEquals(username2, result.getUsername()),
                () -> assertEquals(password2, result.getPassword()), // (Es la contraseña E N C R I P T A D A)
                () -> assertEquals(dname2, result.getDisplayName()),
                () -> assertEquals(email2, result.getEmail())
        );
        verify(userRepository,times(1)).findByUsername(username2);
    }

    @Test
    void testFindUserByUsernameThrowsResourceNotFoundException() {
        String unexistentUsername = "i dont exist";
        when(userRepository.findByUsername(unexistentUsername)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->userService.findByUsername(unexistentUsername));
        verify(userRepository,times(1)).findByUsername(unexistentUsername);
    }

    @Test
    void testSaveUserSavesUserSuccessfully() {
        User mockUserToSave = new User(null,username2,password2,dname2,email2,Set.of(),null,Set.of());
        User mockSavedUser = new User(id2,username2,password2,dname2,email2,Set.of(),null,Set.of());
        when(userRepository.save(mockUserToSave)).thenReturn(mockSavedUser);

        User result = userService.save(mockUserToSave);

        assertAll("Validando los campos del User",
                () -> assertNotNull(result),
                () -> assertNotNull(result.getId()),
                () -> assertEquals(username2, result.getUsername()),
                () -> assertEquals(password2, result.getPassword()), // (Es la contraseña E N C R I P T A D A)
                () -> assertEquals(dname2, result.getDisplayName()),
                () -> assertEquals(email2, result.getEmail())
        );
        verify(userRepository,times(1)).save(mockUserToSave);
    }

    @Test
    void testRegisterUserRegistersUserSuccessfully() {
        User mockSavedUser = new User(id2,username2,password2,dname2,email2,Set.of(),null,Set.of());
        when(userRepository.existsByUsername(username2)).thenReturn(false);
        when(userRepository.existsByEmail(email2)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername(username2);
        registerRequestDto.setEmail(email2);
        registerRequestDto.setUnencryptedPassword("1234");
        when(passwordEncoder.encode("1234")).thenReturn(password2);
        User result = userService.register(registerRequestDto);

        assertAll("Validando los campos del User",
                () -> assertNotNull(result),
                () -> assertNotNull(result.getId()),
                () -> assertEquals(username2, result.getUsername()),
                () -> assertEquals(password2, result.getPassword()), // (Es la contraseña E N C R I P T A D A)
                () -> assertEquals(dname2, result.getDisplayName()),
                () -> assertEquals(email2, result.getEmail())
        );
        verify(userRepository,times(1)).existsByUsername(username2);
        verify(userRepository,times(1)).existsByEmail(email2);
        verify(passwordEncoder,times(1)).encode("1234");
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserWithExistingUsernameThrowsUserAlreadyExistsException() {
        when(userRepository.existsByUsername(username2)).thenReturn(true);

        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername(username2);
        assertThrows(UserAlreadyExistsException.class,()->userService.register(registerRequestDto));
        verify(userRepository,times(1)).existsByUsername(username2);
        verify(passwordEncoder,never()).encode(anyString());
        verify(userRepository,never()).save(any(User.class));
    }

    @Test
    void testRegisterUserWithExistingEmailThrowsUserAlreadyExistsException() {
        when(userRepository.existsByEmail(email2)).thenReturn(true);

        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setEmail(email2);
        assertThrows(UserAlreadyExistsException.class,()->userService.register(registerRequestDto));
        verify(userRepository,times(1)).existsByEmail(email2);
        verify(passwordEncoder,never()).encode(anyString());
        verify(userRepository,never()).save(any(User.class));
    }

    @Test
    void testDeleteUserByIdDeletesUserSuccessfully() {
        when(userRepository.existsById(id2)).thenReturn(true);

        assertDoesNotThrow(()->userService.deleteById(id2),"El método falló y lanzó una excepción, debería haber terminado con éxito y silenciosamente");
        verify(userRepository,times(1)).existsById(id2);
        verify(userRepository,times(1)).deleteById(id2);
    }

    @Test
    void testDeleteUserByIdThrowsResourceNotFoundException() {
        Long unexistentId = 99L;
        when(userRepository.existsById(unexistentId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,()->userService.deleteById(unexistentId));
        verify(userRepository,times(1)).existsById(unexistentId);
        verify(userRepository,never()).deleteById(anyLong());
    }

    @Test
    void testDeleteUserByEmailDeletesUserSuccessfully() {
        when(userRepository.existsByEmail(email2)).thenReturn(true);

        assertDoesNotThrow(()->userService.deleteByEmail(email2),"El método falló y lanzó una excepción, debería haber terminado con éxito y silenciosamente");
        verify(userRepository,times(1)).existsByEmail(email2);
        verify(userRepository,times(1)).deleteByEmail(email2);
    }

    @Test
    void testDeleteUserByEmailThrowsResourceNotFoundException() {
        String unexistentEmail = "unexistent@test.com";
        when(userRepository.existsByEmail(unexistentEmail)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,()->userService.deleteByEmail(unexistentEmail));
        verify(userRepository,times(1)).existsByEmail(unexistentEmail);
        verify(userRepository,never()).deleteByEmail(anyString());
    }


    @Test
    void testDeleteUserByUsernameDeletesUserSuccessfully() {
        when(userRepository.existsByUsername(username2)).thenReturn(true);

        assertDoesNotThrow(()->userService.deleteByUsername(username2),"El método falló y lanzó una excepción, debería haber terminado con éxito y silenciosamente");
        verify(userRepository,times(1)).existsByUsername(username2);
        verify(userRepository,times(1)).deleteByUsername(username2);
    }

    @Test
    void testDeleteUserByUsernameThrowsResourceNotFoundException() {
        String unexistentUsername = "unexistent";
        when(userRepository.existsByUsername(unexistentUsername)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,()->userService.deleteByUsername(unexistentUsername));
        verify(userRepository,times(1)).existsByUsername(unexistentUsername);
        verify(userRepository,never()).deleteByUsername(anyString());
    }

    @Test
    void testUserHasPortfolioReturnsTrue() {
        when(userRepository.existsByUsername(username1)).thenReturn(true);
        when(userRepository.hasPortfolioByUsername(username1)).thenReturn(true);

        boolean result = userService.hasPortfolio(username1);

        assertTrue(result);
        verify(userRepository,times(1)).existsByUsername(username1);
        verify(userRepository,times(1)).hasPortfolioByUsername(username1);
    }

    @Test
    void testUserWithoutPortfolioHasPortfolioReturnsFalse() {
        when(userRepository.existsByUsername(username1)).thenReturn(true);
        when(userRepository.hasPortfolioByUsername(username1)).thenReturn(false);

        boolean result = userService.hasPortfolio(username1);

        assertFalse(result);
        verify(userRepository,times(1)).existsByUsername(username1);
        verify(userRepository,times(1)).hasPortfolioByUsername(username1);
    }

    @Test
    void testInexistentUserHasPortfolioThrowsResourceNotFoundException() {
        when(userRepository.existsByUsername(username1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,()->userService.hasPortfolio(username1));
        verify(userRepository,times(1)).existsByUsername(username1);
        verify(userRepository,never()).hasPortfolioByUsername(username1);
    }


}