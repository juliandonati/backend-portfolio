package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Degree;
import com.juliandonati.backendPortafolio.dto.DegreeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.DegreeMapper;
import com.juliandonati.backendPortafolio.mapper.DegreeMapperImpl;
import com.juliandonati.backendPortafolio.repository.DegreeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DegreeServiceImplTest {
    @Mock
    DegreeRepository degreeRepository;
    @Spy
    DegreeMapper degreeMapper = new DegreeMapperImpl();

    @InjectMocks
    DegreeServiceImpl degreeService;



    @Test
    void testFindAllReturnsListOfDegrees() {
        // Arrange
        List<Degree> mockDegrees = List.of(
                new Degree(4L,"Java","Curso intensivo en Backend Coding", LocalDate.now() , null,null,null),
                new Degree(6L, "Typescript", "Curso en Frontend Coding", LocalDate.now(), null, null, null));
        when(degreeRepository.findAll()).thenReturn(mockDegrees);

        // Act
        List<DegreeDto> result = degreeService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals(4L,result.getFirst().getId());
        assertEquals("Java",result.getFirst().getName());
        assertEquals(6L,result.get(1).getId());
        assertEquals("Typescript",result.get(1).getName());
        verify(degreeRepository,times(1)).findAll();
        verify(degreeMapper,times(2)).toDto(any(Degree.class));
    }

    @Test
    void testFindByIdReturnsDegree() {
        // Arrange
        Long mockId = 87L;
        String mockName = "Software Engineering";
        String mockDesc = "Great at developing and shipping SW!";
        Degree mockDegree = new Degree(mockId, mockName, mockDesc, LocalDate.now(), null, null, null);
        when(degreeRepository.findById(mockId)).thenReturn(Optional.of(mockDegree));

        // Act
        DegreeDto result = degreeService.findById(mockId);

        // Assert
        assertNotNull(result);
        assertEquals(mockId,result.getId());
        assertEquals(mockName,result.getName());
        assertEquals(mockDesc,result.getDescription());
        verify(degreeRepository,times(1)).findById(mockId);
        verify(degreeMapper,times(1)).toDto(mockDegree);
    }

    @Test
    void testFindByIdThrowsResourceNotFoundException() {
        // Arrange
        Long mockId = 999L;
        when(degreeRepository.findById(mockId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->degreeService.findById(mockId));
        verify(degreeRepository,times(1)).findById(mockId);
        verify(degreeMapper,never()).toDto(any(Degree.class));
    }

    @Test
    void testUpdateDegreeUpdatesDegreeSuccessfully() {
        // Arrange
        Long mockId = 45L;
        String mockOldName = "Nuclear Engineerinj";
        LocalDate mockStartDate = LocalDate.now();
        Degree mockOldDegree = new Degree(mockId,mockOldName,null,mockStartDate,null,null,null);
        when(degreeRepository.findById(mockId)).thenReturn(Optional.of(mockOldDegree));

        String mockNewName = "Nuclear Engineering";
        Degree mockUpdatedDegree = new Degree(mockId,mockNewName,null,mockStartDate,null,null,null);
        when(degreeRepository.save(any(Degree.class))).thenReturn(mockUpdatedDegree);

        // Act
        DegreeDto mockNewDegreeDto = new DegreeDto(null,mockNewName,null,mockStartDate,null,null);
        DegreeDto result = degreeService.update(mockNewDegreeDto,mockId);

        // Assert
        assertNotNull(result);
        assertEquals(mockId,result.getId());
        assertEquals(mockNewName,result.getName());
        assertEquals(mockStartDate,result.getStartDate());
        verify(degreeMapper,times(1)).updateEntity(mockNewDegreeDto,mockOldDegree);
        verify(degreeRepository,times(1)).save(any(Degree.class));
        verify(degreeMapper,times(1)).toDto(mockUpdatedDegree);
    }

    @Test
    void testUpdateDegreeThrowsResourceNotFoundException(){
        // Arrange
        Long mockId = 999L;
        DegreeDto mockNewDegreeDto = new DegreeDto(null,"Generic Degree Title",null,LocalDate.now(),null,null);
        when(degreeRepository.findById(mockId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->degreeService.update(mockNewDegreeDto,mockId));
        verify(degreeRepository,times(1)).findById(mockId);
        verify(degreeMapper,never()).updateEntity(eq(mockNewDegreeDto),any(Degree.class));
        verify(degreeRepository,never()).save(any(Degree.class));
        verify(degreeMapper,never()).toDto(any(Degree.class));
    }

    @Test
    void testDeleteDegreeByIdDeletesDegreeSuccessfully() {
        // Arrange
        Long mockId = 23L;
        when(degreeRepository.existsById(mockId)).thenReturn(true);

        // Act + Assert
        assertDoesNotThrow(()->degreeService.deleteById(mockId),
                "El método falló y lanzó una excepción, cuando debió haber finalizado con éxito y silenciosamente");
        verify(degreeRepository,times(1)).existsById(mockId);
        verify(degreeRepository,times(1)).deleteById(mockId);
    }

    @Test
    void testDeleteDegreeByIdThrowsResourceNotFoundException(){
        // Arrange
        Long mockId = 999L;
        when(degreeRepository.existsById(mockId)).thenReturn(false);

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->degreeService.deleteById(mockId));
        verify(degreeRepository,times(1)).existsById(mockId);
        verify(degreeRepository,never()).deleteById(any(Long.class));
    }

    @Test
    void testFindDegreeByOwnerUsernameReturnsDegrees() {
        // Arrange
        String ownerUsername = "pedrito12";
        String mockName = "Generic Degree Title";
        String mockDesc = "Generic Degree Desc";
        Degree mockDegree = new Degree(4L,mockName,mockDesc, LocalDate.now(),null,null,null);
        when(degreeRepository.findByOwnerUsername(ownerUsername)).thenReturn(List.of(mockDegree,new Degree()));

        // Act
        List<DegreeDto> result = degreeService.findByOwnerUsername(ownerUsername);

        // Assert
        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals(4L,result.getFirst().getId());
        assertEquals(mockName,result.getFirst().getName());
        assertEquals(mockDesc,result.getFirst().getDescription());
        verify(degreeRepository,times(1)).findByOwnerUsername(ownerUsername);
        verify(degreeMapper,times(2)).toDto(any(Degree.class));
    }

    @Test
    void testFindImgUrlByDegreeIdReturnsImgUrl() {
        // Arrange
        Long mockId = 32L;
        String mockImgUrl = "http://imagen-prueba.com.ar";
        when(degreeRepository.findImgUrlByDegreeId(mockId)).thenReturn(Optional.of(mockImgUrl));

        // Act
        String result = degreeService.findImgUrlByDegreeId(mockId);

        // Assert
        assertNotNull(result);
        assertEquals(mockImgUrl,result);
        verify(degreeRepository,times(1)).findImgUrlByDegreeId(mockId);
    }

    @Test
    void testFindImgUrlByDegreeIdThrowsResourceNotFoundException(){
        // Arrange
        Long mockId = 999L;
        when(degreeRepository.findImgUrlByDegreeId(mockId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->degreeService.findImgUrlByDegreeId(mockId));
        verify(degreeRepository,times(1)).findImgUrlByDegreeId(mockId);
    }

    @Test
    void testFindOwnerUsernameByDegreeIdReturnsOwnerUsername() {
        // Arrange
        Long mockId = 2L;
        String ownerUsername = "carla2002";
        when(degreeRepository.findOwnerUsernameByDegreeId(mockId)).thenReturn(Optional.of(ownerUsername));

        // Act
        String result = degreeService.findOwnerUsernameByDegreeId(mockId);

        // Assert
        assertNotNull(result);
        assertEquals(ownerUsername,result);
        verify(degreeRepository,times(1)).findOwnerUsernameByDegreeId(mockId);
    }

     @Test
    void testFindOwnerUsernameByDegreeIdThrowsResourceNotFoundException() {
        Long mockId = 88L;
        when(degreeRepository.findOwnerUsernameByDegreeId(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->degreeService.findOwnerUsernameByDegreeId(mockId));
        verify(degreeRepository,times(1)).findOwnerUsernameByDegreeId(mockId);
     }
}