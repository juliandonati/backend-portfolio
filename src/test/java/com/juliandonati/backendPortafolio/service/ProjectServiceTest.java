package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.domain.Project;
import com.juliandonati.backendPortafolio.dto.ProjectDto;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import com.juliandonati.backendPortafolio.repository.ProjectRepository;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProjectServiceTest {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;
    private final EntityManager entityManager;

    @Autowired
    public ProjectServiceTest(ProjectService projectService,
                              ProjectRepository projectRepository,
                              UserRepository userRepository,
                              PortfolioService portfolioService,
                              PortfolioRepository portfolioRepository,
                              EntityManager entityManager) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.portfolioService = portfolioService;
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    private final String title1 = "TITULO PROYECTO UNO";
    private final String desc1 = "DESCRIPCIÓN PROYECTO UNO";
    private final LocalDate startDate1 = LocalDate.of(2020, 10, 10);
    private final LocalDate endDate1 = LocalDate.of(2022, 11, 9);
    private final String url1 = "https://url1proyecto.com";
    private final String imgUrl1 = "https://urlimgproyectouno.com";

    @Test
    void testFindProjectsByOwnerUsernameReturnsListOfProjects() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        String title2 = "TITULO PROYECTO 2";
        String desc2 = "DESCRIPCIÓN PROYECTO DOS";
        LocalDate startDate2 = LocalDate.of(2020, 9, 15);
        LocalDate endDate2 = LocalDate.of(2023, 1, 2);
        String url2 = "https://urldosproyecto.com";
        String imgUrl2 = "https://urlimgproyecto2.com";
        portfolio.addProject(new Project(null, title1, desc1, startDate1, endDate1, url1, imgUrl1, null));
        portfolio.addProject(new Project(null, title2, desc2, startDate2, endDate2, url2, imgUrl2, null));
        portfolioRepository.save(portfolio);
        // Act
        List<ProjectDto> result = projectService.findProjectsByOwnerUsername(TEST_OWNER_USERNAME);
        ProjectDto result1 = result.stream().filter(p -> p.getTitle().equals(title1)).findFirst()
                .orElseThrow(() -> new AssertionError("No cargó correctamente el proyecto de prueba 1"));
        ProjectDto result2 = result.stream().filter(p -> p.getTitle().equals(title2)).findFirst()
                .orElseThrow(() -> new AssertionError("No cargó correctamente el proyecto de prueba 2"));
        // Assert
        assertAll("Validando los campos del proyecto",
                () -> assertEquals(2, result.size()),
                () -> assertEquals(title1, result1.getTitle()),
                () -> assertEquals(desc1, result1.getDescription()),
                () -> assertEquals(startDate1, result1.getStartDate()),
                () -> assertEquals(endDate1, result1.getEndDate()),
                () -> assertEquals(url1, result1.getUrl()),
                () -> assertEquals(imgUrl1, result1.getImgUrl()),
                () -> assertEquals(title2, result2.getTitle()),
                () -> assertEquals(desc2, result2.getDescription()),
                () -> assertEquals(startDate2, result2.getStartDate()),
                () -> assertEquals(endDate2, result2.getEndDate()),
                () -> assertEquals(url2, result2.getUrl()),
                () -> assertEquals(imgUrl2, result2.getImgUrl())
        );
    }

    @Test
    void testFindImgUrlByProjectIdReturnsImgUrlSuccessfully() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.addProject(new Project(null, title1, desc1, startDate1, endDate1, url1, imgUrl1, null));
        Long projectId = portfolioRepository.save(portfolio).getProjects().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No cargó correctamente el proyecto de prueba"))
                .getId();

        // Act
        String result = projectService.findImgUrlByProjectId(projectId);

        // Assert
        assertAll("Validando String recibido",
                () -> assertNotNull(result),
                () -> assertEquals(imgUrl1, result)
        );
    }

    @Test
    void testFindOwnerUsernameByProjectIdReturnsOwnerUsernameSuccessfully() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.addProject(new Project(null, title1, desc1, startDate1, endDate1, url1, imgUrl1, null));
        Long projectId = portfolioRepository.save(portfolio).getProjects().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No cargó correctamente el proyecto de prueba"))
                .getId();

        // Act
        String result = projectService.findOwnerUsernameByProjectId(projectId);

        // Assert
        assertAll("Validando String recibido",
                () -> assertNotNull(result),
                () -> assertEquals(TEST_OWNER_USERNAME, result)
        );
    }

    @Test
    void testProjectCRUDLifeCycle() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.addProject(new Project(null, title1, desc1, startDate1, endDate1, url1, imgUrl1, null));
        // CREATE
        Project savedProject = portfolioService.save(portfolio).getProjects().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No cargó correctamente el proyecto de prueba"));
        Long projectId = savedProject.getId();

        assertAll("Validando los campos del proyecto",
                () -> assertNotNull(projectId),
                () -> assertEquals(title1, savedProject.getTitle()),
                () -> assertEquals(desc1, savedProject.getDescription()),
                () -> assertEquals(startDate1, savedProject.getStartDate()),
                () -> assertEquals(endDate1, savedProject.getEndDate()),
                () -> assertEquals(url1, savedProject.getUrl()),
                () -> assertEquals(imgUrl1, savedProject.getImgUrl())
        );
        // READ
        ProjectDto searchedProjectDto = projectService.findById(projectId);
        assertAll("Validando los campos del proyecto",
                () -> assertEquals(projectId, searchedProjectDto.getId()),
                () -> assertEquals(title1, searchedProjectDto.getTitle()),
                () -> assertEquals(desc1, searchedProjectDto.getDescription()),
                () -> assertEquals(startDate1, searchedProjectDto.getStartDate()),
                () -> assertEquals(endDate1, searchedProjectDto.getEndDate()),
                () -> assertEquals(url1, searchedProjectDto.getUrl()),
                () -> assertEquals(imgUrl1, searchedProjectDto.getImgUrl())
        );
        // UPDATE
        String newTitle = "NEW TITULO!!!";
        String newDesc = "DESCRIPCIÓN PROYECTO NUEVAA";
        LocalDate newStartDate = LocalDate.of(2019, 10, 15);
        LocalDate newEndDate = null;
        String newUrl = "https://urlnuevaproyecto.com";
        String newImgUrl = "https://urlimgproyectonuevo.com";
        ProjectDto newProjectDto = new ProjectDto(null, newTitle, newDesc, newStartDate, newEndDate, newUrl, newImgUrl);

        ProjectDto updatedProjectDto = projectService.update(newProjectDto, projectId);

        assertAll("Validando los campos del proyecto",
                () -> assertEquals(projectId, updatedProjectDto.getId()),
                () -> assertEquals(newTitle, updatedProjectDto.getTitle()),
                () -> assertEquals(newDesc, updatedProjectDto.getDescription()),
                () -> assertEquals(newStartDate, updatedProjectDto.getStartDate()),
                () -> assertEquals(newEndDate, updatedProjectDto.getEndDate()),
                () -> assertEquals(newUrl, updatedProjectDto.getUrl()),
                () -> assertEquals(newImgUrl, updatedProjectDto.getImgUrl())
        );
        // DELETE
        entityManager.clear();
        assertDoesNotThrow(() -> {
                    projectService.deleteById(projectId);
                    entityManager.flush();
                }
                , TEST_THROWS_MESSAGE);
        assertFalse(projectRepository.existsById(projectId));
    }
}