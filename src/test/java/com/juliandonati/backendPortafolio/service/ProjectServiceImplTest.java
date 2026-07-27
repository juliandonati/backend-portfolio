package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Project;
import com.juliandonati.backendPortafolio.dto.ProjectDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.ProjectMapper;
import com.juliandonati.backendPortafolio.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.TEST_THROWS_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @InjectMocks
    private ProjectServiceImpl projectService;

    private final Long id1 = 7L;
    private final String title1 = "PRIMER TITULO";
    private final String desc1 = "PRIMERA DESCRIPCIÓN";
    private final LocalDate startDate1 = LocalDate.of(2019, 5, 12);
    private final LocalDate endDate1 = LocalDate.of(2021, 5, 12);
    private final String url1 = "https://primeraurl.com";
    private final String imgUrl1 = "https://primeraimgurl.com";

    private final Long id2 = 7L;
    private final String title2 = "2do TITULO";
    private final String desc2 = "2da DESCRIPCIÓN";
    private final LocalDate startDate2 = LocalDate.of(2029, 5, 22);
    private final LocalDate endDate2 = LocalDate.of(2032, 5, 22);
    private final String url2 = "https://2daurl.com";
    private final String imgUrl2 = "https://2daimgurl.com";

    @Test
    void testFindProjectsByOwnerUsernameReturnsListOfProjects() {
        // Arrange
        Project mockProject1 = new Project(id1, title1, desc1, startDate1, endDate1, url1, imgUrl1, null);
        Project mockProject2 = new Project(id2, title2, desc2, startDate2, endDate2, url2, imgUrl2, null);
        String username = "usuariopepito";
        when(projectRepository.findByOwnerUsername(username)).thenReturn(List.of(mockProject1, mockProject2));

        // Act
        List<ProjectDto> result = projectService.findProjectsByOwnerUsername(username);
        ProjectDto result1 = result.getFirst();
        ProjectDto result2 = result.getLast();

        // Assert
        assertAll("Validando campos de los ProjectDto",
                () -> assertEquals(2,result.size()),
                () -> assertEquals(id1,result1.getId()),
                () -> assertEquals(title1,result1.getTitle()),
                () -> assertEquals(desc1,result1.getDescription()),
                () -> assertEquals(startDate1,result1.getStartDate()),
                () -> assertEquals(endDate1,result1.getEndDate()),
                () -> assertEquals(url1,result1.getUrl()),
                () -> assertEquals(imgUrl1,result1.getImgUrl()),
                () -> assertEquals(id2,result2.getId()),
                () -> assertEquals(title2,result2.getTitle()),
                () -> assertEquals(desc2,result2.getDescription()),
                () -> assertEquals(startDate2,result2.getStartDate()),
                () -> assertEquals(endDate2,result2.getEndDate()),
                () -> assertEquals(url2,result2.getUrl()),
                () -> assertEquals(imgUrl2,result2.getImgUrl())
        );
        verify(projectRepository,times(1)).findByOwnerUsername(username);
        verify(projectMapper,times(2)).toDto(any(Project.class));
    }

    @Test
    void testDeleteProjectByIdDeletesProjectSuccessfully() {
        // Arrange
        when(projectRepository.existsById(id1)).thenReturn(true);
        // Act + Assert
        assertDoesNotThrow(()->projectService.deleteById(id1),TEST_THROWS_MESSAGE);
        verify(projectRepository,times(1)).existsById(id1);
        verify(projectRepository,times(1)).deleteById(id1);
    }

    @Test
    void testDeleteProjectByIdThrowsResourceNotFoundException() {
        // Arrange
        Long mockUnexistentId = 99L;
        when(projectRepository.existsById(mockUnexistentId)).thenReturn(false);
        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->projectService.deleteById(mockUnexistentId));
        verify(projectRepository,times(1)).existsById(mockUnexistentId);
        verify(projectRepository,never()).deleteById(anyLong());
    }

    @Test
    void testFindAllProjectsReturnsListOfProjects() {
        // Arrange
        Project mockProject1 = new Project(id1, title1, desc1, startDate1, endDate1, url1, imgUrl1, null);
        Project mockProject2 = new Project(id2, title2, desc2, startDate2, endDate2, url2, imgUrl2, null);
        when(projectRepository.findAll()).thenReturn(List.of(mockProject1, mockProject2));

        // Act
        List<ProjectDto> result = projectService.findAll();
        ProjectDto result1 = result.getFirst();
        ProjectDto result2 = result.getLast();

        // Assert
        assertAll("Validando campos de los ProjectDto",
                () -> assertEquals(2,result.size()),
                () -> assertEquals(id1,result1.getId()),
                () -> assertEquals(title1,result1.getTitle()),
                () -> assertEquals(desc1,result1.getDescription()),
                () -> assertEquals(startDate1,result1.getStartDate()),
                () -> assertEquals(endDate1,result1.getEndDate()),
                () -> assertEquals(url1,result1.getUrl()),
                () -> assertEquals(imgUrl1,result1.getImgUrl()),
                () -> assertEquals(id2,result2.getId()),
                () -> assertEquals(title2,result2.getTitle()),
                () -> assertEquals(desc2,result2.getDescription()),
                () -> assertEquals(startDate2,result2.getStartDate()),
                () -> assertEquals(endDate2,result2.getEndDate()),
                () -> assertEquals(url2,result2.getUrl()),
                () -> assertEquals(imgUrl2,result2.getImgUrl())
                );
        verify(projectRepository,times(1)).findAll();
        verify(projectMapper,times(2)).toDto(any(Project.class));
    }

    @Test
    void testFindProjectByIdReturnsProjectSuccessfully() {
        // Arrange
        Project mockProject = new Project(id1, title1, desc1, startDate1, endDate1, url1, imgUrl1, null);
        when(projectRepository.findById(id1)).thenReturn(Optional.of(mockProject));
        // Act
        ProjectDto result = projectService.findById(id1);
        // Assert
        assertAll("Validando campos de los ProjectDto",
                () -> assertEquals(id1,result.getId()),
                () -> assertEquals(title1,result.getTitle()),
                () -> assertEquals(desc1,result.getDescription()),
                () -> assertEquals(startDate1,result.getStartDate()),
                () -> assertEquals(endDate1,result.getEndDate()),
                () -> assertEquals(url1,result.getUrl()),
                () -> assertEquals(imgUrl1,result.getImgUrl())
        );
        verify(projectRepository,times(1)).findById(id1);
        verify(projectMapper,times(1)).toDto(mockProject);
    }

    @Test
    void testFindProjectByIdThrowsResourceNotFoundException() {
        // Arrange
        Long mockUnexistentId = 99L;
        when(projectRepository.findById(mockUnexistentId)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->projectService.findById(mockUnexistentId));
        verify(projectRepository,times(1)).findById(mockUnexistentId);
        verify(projectMapper,never()).toDto(any(Project.class));
    }

    @Test
    void testUpdateProjectUpdatesProjectSuccessfully() {
        // Arrange
        Project mockOldProject = new Project(id1, title1, desc1, startDate1, endDate1, url1, imgUrl1, null);
        String newTitle = "NUEVO TITULO";
        String newDesc = "NUEVA DESCRIPCIÓN!!!!";
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = null;
        String newUrl = "https://newurl.com";
        String newImgUrl = "https://newimgurl.com";
        Project mockUpdatedProject = new Project(id1,newTitle,newDesc,newStartDate,newEndDate,newUrl,newImgUrl,null);
        when(projectRepository.findById(id1)).thenReturn(Optional.of(mockOldProject));
        when(projectRepository.save(any(Project.class))).thenReturn(mockUpdatedProject);
        // Act
        ProjectDto mockProjectDto = new ProjectDto(null,newTitle,newDesc,newStartDate,newEndDate,newUrl,newImgUrl);
        ProjectDto result = projectService.update(mockProjectDto,id1);
        // Assert
        assertAll("Validando campos de los ProjectDto",
                () -> assertEquals(id1,result.getId()),
                () -> assertEquals(newTitle,result.getTitle()),
                () -> assertEquals(newDesc,result.getDescription()),
                () -> assertEquals(newStartDate,result.getStartDate()),
                () -> assertEquals(newEndDate,result.getEndDate()),
                () -> assertEquals(newUrl,result.getUrl()),
                () -> assertEquals(newImgUrl,result.getImgUrl())
        );
        verify(projectRepository,times(1)).findById(id1);
        verify(projectMapper,times(1)).updateEntity(mockProjectDto,mockOldProject);
        verify(projectRepository,times(1)).save(mockUpdatedProject);
    }

    @Test
    void testUpdateProjectThrowsResourceNotFoundException() {
        // Arrange
        Long mockUnexistentId = 99L;
        when(projectRepository.findById(mockUnexistentId)).thenReturn(Optional.empty());
        // Act + Assert
        assertThrows(ResourceNotFoundException.class,()->projectService.update(new ProjectDto(),mockUnexistentId));
        verify(projectRepository,times(1)).findById(mockUnexistentId);
        verify(projectMapper,never()).updateEntity(any(ProjectDto.class),any(Project.class));
        verify(projectRepository,never()).save(any(Project.class));
    }

}