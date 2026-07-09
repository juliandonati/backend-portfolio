package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.AboutMe;
import com.juliandonati.backendPortafolio.dto.AboutMeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.AboutMeMapper;
import com.juliandonati.backendPortafolio.mapper.AboutMeMapperImpl;
import com.juliandonati.backendPortafolio.repository.AboutMeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AboutMeServiceImplTest {
    @Mock
    AboutMeRepository aboutMeRepository;
    @Spy
    AboutMeMapper aboutMeMapper = new AboutMeMapperImpl();

    @InjectMocks
    private AboutMeServiceImpl aboutMeService;

    @Test
    void testFindAllReturnsListOfAboutMeDtos() {
        // Arrange
        List<AboutMe> mockAboutMes = Arrays.asList(new AboutMe(), new AboutMe());
        when(aboutMeRepository.findAll()).thenReturn(mockAboutMes);

        // Act
        List<AboutMeDto> result = aboutMeService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2,result.size());
        verify(aboutMeMapper,times(2)).toDto(any(AboutMe.class));
        verify(aboutMeRepository,times(1)).findAll();
    }

    @Test
    void testFindByIdReturnsAboutMe() {
        // Arrange
        Long mockAboutMeId = 1L;
        String mockAboutMeTitle = "Sobre mí";
        String mockAboutMeDesc = "Me gusta tomar café";
        AboutMe mockAboutMe = new AboutMe(mockAboutMeId,mockAboutMeTitle,mockAboutMeDesc,null,null,null,null);
        when(aboutMeRepository.findById(mockAboutMeId)).thenReturn(Optional.of(mockAboutMe));

        // Act
        AboutMeDto result = aboutMeService.findById(mockAboutMeId);

        // Assert
        assertNotNull(result);
        assertEquals(mockAboutMeId,result.getId());
        assertEquals(mockAboutMeTitle,result.getTitle());
        assertEquals(mockAboutMeDesc,result.getDescription());
        verify(aboutMeMapper,times(1)).toDto(mockAboutMe);
        verify(aboutMeRepository,times(1)).findById(mockAboutMeId);
    }

    @Test
    void testFindByIdThrowsResourceNotFoundException(){
        // Arrange
        Long mockAboutMeId = 99L;
        when(aboutMeRepository.findById(mockAboutMeId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> aboutMeService.findById(mockAboutMeId));
        verify(aboutMeMapper,never()).toDto(any(AboutMe.class));
        verify(aboutMeRepository,times(1)).findById(mockAboutMeId);
    }

    @Test
    void testSaveAboutMeSavesValidAboutMe() {
        // Arrange
        String mockAboutMeTitle = "Sobre mí";
        String mockAboutMeDesc = "Me gusta tomar café";
        AboutMe mockAboutMeSaved = new AboutMe(1L,mockAboutMeTitle,mockAboutMeDesc,null,null,null,null);
        when(aboutMeRepository.save(any(AboutMe.class))).thenReturn(mockAboutMeSaved);

        // Act
        AboutMeDto mockAboutMeDto = new AboutMeDto(null,mockAboutMeTitle,mockAboutMeDesc,null,null,null);
        AboutMeDto result = aboutMeService.save(mockAboutMeDto);


        // Assert
        assertNotNull(result);
        assertEquals(mockAboutMeTitle,result.getTitle());
        assertEquals(mockAboutMeDesc,result.getDescription());
        verify(aboutMeMapper,times(1)).toEntity(mockAboutMeDto);
        verify(aboutMeMapper,times(1)).toDto(mockAboutMeSaved);
        verify(aboutMeRepository,times(1)).save(any(AboutMe.class));
    }

    @Test
    void testUpdateAboutMeUpdatesAboutMeSuccessfully() {
        // Arrange
        AboutMe mockOldAboutMe = new AboutMe(3L,"Sobre mí",null,null,null,null,null);
        when(aboutMeRepository.findById(3L)).thenReturn(Optional.of(mockOldAboutMe));

        String mockAboutMeUpdatedTitle = "Deberías saber que:";
        AboutMeDto mockNewAboutMeDto = new AboutMeDto(null,mockAboutMeUpdatedTitle,null,null,null,null);
        AboutMe mockUpdatedAboutMe = new AboutMe(3L,mockAboutMeUpdatedTitle,null,null,null,null,null);
        when(aboutMeRepository.save(any(AboutMe.class))).thenReturn(mockUpdatedAboutMe);

        // Act
        AboutMeDto result = aboutMeService.update(mockNewAboutMeDto,3L);

        // Assert
        assertNotNull(result);
        assertEquals(3L,result.getId());
        assertEquals(mockAboutMeUpdatedTitle,result.getTitle());
        verify(aboutMeMapper,times(1)).updateEntity(mockNewAboutMeDto,mockOldAboutMe);
        verify(aboutMeMapper,times(1)).toDto(mockUpdatedAboutMe);
        verify(aboutMeRepository,times(1)).findById(3L);
        verify(aboutMeRepository,times(1)).save(any(AboutMe.class));
    }

    @Test
    void testUpdateAboutMeThrowsResourceNotFoundException(){
        // Arrange
        Long mockId = 999L;
        AboutMeDto mockNewAboutMeDto = new AboutMeDto(null, "About me:", "I like doing things", null, null, null);
        when(aboutMeRepository.findById(mockId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->aboutMeService.update(mockNewAboutMeDto,mockId));
        verify(aboutMeRepository,times(1)).findById(mockId);
        verify(aboutMeMapper,never()).updateEntity(eq(mockNewAboutMeDto),any(AboutMe.class));
        verify(aboutMeRepository,never()).save(any(AboutMe.class));
        verify(aboutMeMapper,never()).toDto(any(AboutMe.class));
    }

    @Test
    void testFindByOwnerUsernameReturnsAboutMe() {
        // Arrange
        Long mockAboutMeId = 45L;
        String mockAboutMeTitle = "Sobre mí";
        String mockAboutMeDesc = "Codifico back-ends excepcionales";
        AboutMe mockAboutMe = new AboutMe(mockAboutMeId,mockAboutMeTitle,mockAboutMeDesc,null,null,null,null);

        String mockUsername = "pedro40";

        when(aboutMeRepository.findByOwnerUsername(mockUsername)).thenReturn(Optional.of(mockAboutMe));

        // Act

        AboutMeDto result = aboutMeService.findByOwnerUsername(mockUsername);

        // Assert
        assertNotNull(result);
        assertEquals(mockAboutMeId, result.getId());
        assertEquals(mockAboutMeTitle,result.getTitle());
        assertEquals(mockAboutMeDesc,result.getDescription());
        verify(aboutMeMapper,times(1)).toDto(mockAboutMe);
        verify(aboutMeRepository,times(1)).findByOwnerUsername(mockUsername);
    }

    @Test
    void testFindByOwnerUsernameThrowsResourceNotFoundException() {
        // Arrange
        String ownerUsername = "speedygonzales";
        when(aboutMeRepository.findByOwnerUsername(ownerUsername)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->aboutMeService.findByOwnerUsername(ownerUsername));
        verify(aboutMeRepository,times(1)).findByOwnerUsername(ownerUsername);
        verify(aboutMeMapper,never()).toDto(any(AboutMe.class));
    }

    @Test
    void testExistsByOwnerUsernameReturnsTrue() {
        // Arrange
        String ownerUsername = "maria.carla";
        when(aboutMeRepository.existsByOwnerUsername(ownerUsername)).thenReturn(true);

        // Act
        boolean result = aboutMeService.existsByOwnerUsername(ownerUsername);

        // Assert
        assertTrue(result);
        verify(aboutMeRepository,times(1)).existsByOwnerUsername(ownerUsername);
    }

    @Test
    void testExistsByOwnerUsernameReturnsFalse() {
        // Arrange
        String ownerUsername = "speedygonzales";
        when(aboutMeRepository.existsByOwnerUsername(ownerUsername)).thenReturn(false);

        // Act
        boolean result = aboutMeService.existsByOwnerUsername(ownerUsername);

        // Assert
        assertFalse(result);
        verify(aboutMeRepository,times(1)).existsByOwnerUsername(ownerUsername);
    }
}