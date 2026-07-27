package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.domain.Project;
import com.juliandonati.backendPortafolio.dto.ProjectDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class ProjectMapperTest {
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    private final Long id = 6L;
    private final String title = "Titulo proyecto | PRUEBA";
    private final String desc = "Descripcion proyecto | PRUEBA";
    private final LocalDate startDate = LocalDate.of(2020, 10, 1);
    private final LocalDate endDate = LocalDate.of(2025, 4, 4);
    private final String url = "https://enlaceproyecto.com";
    private final String imgUrl = "https://enlaceimagenproyecto.org";

    @Test
    void testMapProjectEntityToProjectDtoSuccessfully() {
        // Arrange
        Project project = new Project(id, title, desc, startDate, endDate, url, imgUrl, null);

        // Act
        ProjectDto result = projectMapper.toDto(project);

        // Assert
        assertAll("Validando campos del ProjectDto",
                () -> assertNotNull(result),
                () -> assertEquals(id,result.getId()),
                () -> assertEquals(title,result.getTitle()),
                () -> assertEquals(desc,result.getDescription()),
                () -> assertEquals(startDate,result.getStartDate()),
                () -> assertEquals(endDate,result.getEndDate()),
                () -> assertEquals(url,result.getUrl()),
                () -> assertEquals(imgUrl,result.getImgUrl())
        );
    }

    @Test
    void testMapProjectDtoToProjectEntitySuccessfully() {
        // Arrange
        ProjectDto projectDto = new ProjectDto(id, title, desc, startDate, endDate, url, imgUrl);

        // Act
        Project result = projectMapper.toEntity(projectDto);

        // Assert
        assertAll("Validando campos del ProjectDto",
                () -> assertNotNull(result),
                () -> assertEquals(id,result.getId()),
                () -> assertEquals(title,result.getTitle()),
                () -> assertEquals(desc,result.getDescription()),
                () -> assertEquals(startDate,result.getStartDate()),
                () -> assertEquals(endDate,result.getEndDate()),
                () -> assertEquals(url,result.getUrl()),
                () -> assertEquals(imgUrl,result.getImgUrl()),
                () -> assertNull(result.getPortfolio())
        );
    }

    @Test
    void testUpdateProjectEntitySuccessfully() {
        // Arrange
        Portfolio portfolio = new Portfolio();
        Project oldProject = new Project(id, title, desc, startDate, endDate, url, imgUrl, portfolio);
        String newTitle = "NUEVO TITULO";
        String newDesc = "NUEVA DESCRIPCIÓN";
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = null;
        String newUrl = "https://nuevourl.com";
        String newImgUrl = "https://nuevoimgurl.test";
        ProjectDto projectDto = new ProjectDto(null,newTitle,newDesc,newStartDate,newEndDate,newUrl,newImgUrl);

        // Act
        Project result = projectMapper.updateEntity(projectDto,oldProject);

        // Assert
        assertAll("Validando campos del ProjectDto",
                () -> assertNotNull(result),
                () -> assertEquals(id,result.getId()),
                () -> assertEquals(newTitle,result.getTitle()),
                () -> assertEquals(newDesc,result.getDescription()),
                () -> assertEquals(newStartDate,result.getStartDate()),
                () -> assertEquals(newEndDate,result.getEndDate()),
                () -> assertEquals(newUrl,result.getUrl()),
                () -> assertEquals(newImgUrl,result.getImgUrl()),
                () -> assertNotNull(result.getPortfolio())
        );
    }
}