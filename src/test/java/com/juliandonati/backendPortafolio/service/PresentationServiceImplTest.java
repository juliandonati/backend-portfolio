package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Presentation;
import com.juliandonati.backendPortafolio.dto.PresentationDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.PresentationMapper;
import com.juliandonati.backendPortafolio.mapper.PresentationMapperImpl;
import com.juliandonati.backendPortafolio.repository.PresentationRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresentationServiceImplTest {
    @Mock
    PresentationRepository presentationRepository;
    @Spy
    PresentationMapper presentationMapper = new PresentationMapperImpl();

    @InjectMocks
    PresentationServiceImpl presentationService;

    @Test
    void testFindAllPresentationsReturnsListOfPresentations() {
        Long mockId1 = 2L, mockId2 = 7L;
        String mockName1 = "Pedro", mockName2= "María";
        String mockTitle1="Ingeniero en Sistemas", mockTitle2 = "Ingeniera en Sistemas de Información";
        List<Presentation> mockPresentations = List.of(
                new Presentation(mockId1,mockName1,mockTitle1,null,null,null,null,null),
                new Presentation(mockId2,mockName2,mockTitle2,null,null,null,null,null),
                new Presentation());
        when(presentationRepository.findAll()).thenReturn(mockPresentations);

        List<PresentationDto> result = presentationService.findAll();

        assertNotNull(result);
        assertEquals(3,result.size());
        assertEquals(mockId1,result.getFirst().getId());
        assertEquals(mockName1,result.getFirst().getName());
        assertEquals(mockTitle1,result.getFirst().getTitle());
        assertEquals(mockId2,result.get(1).getId());
        assertEquals(mockName2,result.get(1).getName());
        assertEquals(mockTitle2,result.get(1).getTitle());
        verify(presentationRepository,times(1)).findAll();
        verify(presentationMapper,times(3)).toDto(any(Presentation.class));
    }

    @Test
    void testFindPresentationByIdReturnsPresentation() {
        Long mockId = 3L;
        String mockName = "Julián";
        String mockTitle = "Estudiante de Ing. en Sistemas";
        Presentation mockPresentation = new Presentation(mockId,mockName,mockTitle,null,null,null,null,null);
        when(presentationRepository.findById(mockId)).thenReturn(Optional.of(mockPresentation));

        PresentationDto result = presentationService.findById(mockId);

        assertNotNull(result);
        assertEquals(mockId,result.getId());
        assertEquals(mockName,result.getName());
        assertEquals(mockTitle,result.getTitle());
        verify(presentationRepository,times(1)).findById(mockId);
        verify(presentationMapper,times(1)).toDto(mockPresentation);
    }

    @Test
    void testFindPresentationByIdThrowsResourceNotFoundException() {
        Long mockId = 99L;
        when(presentationRepository.findById(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->presentationService.findById(mockId));
        verify(presentationRepository,times(1)).findById(mockId);
        verify(presentationMapper,never()).toDto(any(Presentation.class));
    }

    @Test
    void testSavePresentationSavesPresentationSuccessfully() {
        String mockName = "Carlos Larralde", mockTitle = "Consejero Escolar";
        Presentation mockSavedPresentation = new Presentation(1L,mockName,mockTitle,null,null,null,null,null);
        when(presentationRepository.save(any(Presentation.class))).thenReturn(mockSavedPresentation);

        PresentationDto mockNewPresentation = new PresentationDto(null,mockName,mockTitle,null,null,null,null);
        PresentationDto result = presentationService.save(mockNewPresentation);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(mockName,result.getName());
        assertEquals(mockTitle,result.getTitle());
        verify(presentationMapper,times(1)).toEntity(mockNewPresentation);
        verify(presentationRepository,times(1)).save(any(Presentation.class));
        verify(presentationMapper,times(1)).toDto(mockSavedPresentation);
    }

    @Test
    void testUpdatePresentationUpdatesPresentationSuccessfully() {
        Long mockId = 7L;
        String mockOldName= "María", mockOldTitle = "Ingeniera en Sistemas de Información",
        mockNewName = "Carla", mockNewTitle = "Ingeniera en Sistemas";
        Presentation
                mockOldPresentation = new Presentation(mockId,mockOldName,mockOldTitle,null,null,null,null,null) ,
                mockUpdatedPresentation = new Presentation(mockId,mockNewName,mockNewTitle,null,null,null,null,null);
        when(presentationRepository.findById(mockId)).thenReturn(Optional.of(mockOldPresentation));
        when(presentationRepository.save(any(Presentation.class))).thenReturn(mockUpdatedPresentation);

        PresentationDto mockNewPresentation = new PresentationDto(mockId,mockNewName,mockNewTitle,null,null,null,null);
        PresentationDto result = presentationService.update(mockNewPresentation,mockId);

        assertNotNull(result);
        assertEquals(mockId,result.getId());
        assertEquals(mockNewName,result.getName());
        assertEquals(mockNewTitle,result.getTitle());
        verify(presentationMapper,times(1)).updateEntity(mockNewPresentation,mockOldPresentation);
        verify(presentationRepository,times(1)).save(any(Presentation.class));
        verify(presentationMapper,times(1)).toDto(mockUpdatedPresentation);

    }

    @Test
    void testUpdatePresentationThrowsResourceNotFoundException() {
        Long mockId = 99L;
        when(presentationRepository.findById(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->presentationService.update(new PresentationDto(),mockId));
        verify(presentationRepository,times(1)).findById(mockId);
        verify(presentationMapper,never()).updateEntity(any(PresentationDto.class),any(Presentation.class));
        verify(presentationRepository,never()).save(any(Presentation.class));
        verify(presentationMapper,never()).toDto(any(Presentation.class));
    }

    @Test
    void testDeletePresentationByIdDeletesPresentationSuccessfully() {
        Long mockId = 3L;
        when(presentationRepository.existsById(mockId)).thenReturn(true);

        assertDoesNotThrow(()->presentationService.deleteById(mockId),"El método falló y lanzó una excepción, debería haber finalizado con éxito silenciosamente");
        verify(presentationRepository,times(1)).existsById(mockId);
        verify(presentationRepository,times(1)).deleteById(mockId);
    }

    @Test
    void testDeletePresentationByIdThrowsResourceNotFoundException() {
        Long mockId = 99L;
        when(presentationRepository.existsById(mockId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,()->presentationService.deleteById(mockId));
        verify(presentationRepository,times(1)).existsById(mockId);
        verify(presentationRepository,never()).deleteById(anyLong());
    }

    @Test
    void testFindPresentationByOwnerUsernameReturnsPresentation() {
        String ownerUsername = "pedrito.programador";
        Long mockId = 1L;
        String mockName = "Pedro", mockTitle="Ingeniero en Sistemas", mockImgUrl="http://imagenepica.com";
        Presentation mockPresentation = new Presentation(mockId,mockName,mockTitle,null,mockImgUrl,null,null,null);
        when(presentationRepository.findByOwnerUsername(ownerUsername)).thenReturn(Optional.of(mockPresentation));

        PresentationDto result = presentationService.findByOwnerUsername(ownerUsername);

        assertNotNull(result);
        assertEquals(mockId,result.getId());
        assertEquals(mockName,result.getName());
        assertEquals(mockTitle,result.getTitle());
        assertEquals(mockImgUrl,result.getImgUrl());
        verify(presentationRepository,times(1)).findByOwnerUsername(ownerUsername);
        verify(presentationMapper,times(1)).toDto(mockPresentation);
    }

    @Test
    void testFindPresentationImgUrlByOwnerUsernameReturnsImgUrl() {
        String ownerUsername = "pedrito.programador";
        String mockImgUrl="http://imagenepica.com";
        when(presentationRepository.findImgUrlByOwnerUsername(ownerUsername)).thenReturn(Optional.of(mockImgUrl));

        String result = presentationService.findImgUrlByOwnerUsername(ownerUsername);

        assertNotNull(result);
        assertEquals(mockImgUrl,result);
        verify(presentationRepository,times(1)).findImgUrlByOwnerUsername(ownerUsername);
    }

    @Test
    void testFindPresentationImgUrlByOwnerUsernameThrowsResourceNotFoundException() {
        String ownerUsername = "falsousuarionoexistojajaja";
        when(presentationRepository.findImgUrlByOwnerUsername(ownerUsername)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->presentationService.findImgUrlByOwnerUsername(ownerUsername));
        verify(presentationRepository,times(1)).findImgUrlByOwnerUsername(ownerUsername);
    }

    @Test
    void testPresentationExistsByOwnerUsernameReturnsTrue() {
        String ownerUsername = "pedrito.programador";
        when(presentationRepository.existsByOwnerUsername(ownerUsername)).thenReturn(true);

        boolean result = presentationService.existsByOwnerUsername(ownerUsername);

        assertTrue(result);
        verify(presentationRepository,times(1)).existsByOwnerUsername(ownerUsername);
    }

    @Test
    void testPresentationExistsByOwnerUsernameReturnsFalse() {
            String ownerUsername = "pedrito.pprogramador";
            when(presentationRepository.existsByOwnerUsername(ownerUsername)).thenReturn(false);

            boolean result = presentationService.existsByOwnerUsername(ownerUsername);

            assertFalse(result);
            verify(presentationRepository,times(1)).existsByOwnerUsername(ownerUsername);

    }

    @Test
    void testDeletePresentationByOwnerUsernameDeletesPresentationSuccessfully() {
        String ownerUsername = "pedrito.programador";
        when(presentationRepository.existsByOwnerUsername(ownerUsername)).thenReturn(true);

        assertDoesNotThrow(()->presentationService.deleteByOwnerUsername(ownerUsername),"El método falló y lanzó una excepción, debería haber finalizado con éxito silenciosamente");
        verify(presentationRepository,times(1)).existsByOwnerUsername(ownerUsername);
        verify(presentationRepository,times(1)).deleteByOwnerUsername(ownerUsername);
    }

    @Test
    void testDeletePresentationByOwnerUsernameThrowsResourceNotFoundException() {
        String ownerUsername = "pedrrito.programador";
        when(presentationRepository.existsByOwnerUsername(ownerUsername)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,()->presentationService.deleteByOwnerUsername(ownerUsername));
        verify(presentationRepository,times(1)).existsByOwnerUsername(ownerUsername);
        verify(presentationRepository,never()).deleteByOwnerUsername(anyString());
    }


}