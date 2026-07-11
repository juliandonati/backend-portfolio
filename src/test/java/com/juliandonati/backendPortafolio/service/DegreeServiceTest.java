package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Degree;
import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.dto.DegreeDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.createPortfolio;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DegreeServiceTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private DegreeService degreeService;

    private final String ownerUsername = MiscTestUtilities.TEST_OWNER_USERNAME;

    @Test
    void testFindDegreeByOwnerUsernameReturnsListOfDegreesSuccessfully() {
        // Arrange
        Portfolio portfolioToSave = createPortfolio(userRepository);
        String name = "Generic Degree Title";
        String desc = "Generic Degree Desc";
        Degree degreeToSave = new Degree(null,name,desc, LocalDate.now(),null,null,portfolioToSave);
        // Guardamos el Degree en el Repository para no depender del servicio para guardarlo (este test no trata de eso)
        portfolioToSave.setDegrees(Set.of(degreeToSave));
        portfolioRepository.save(portfolioToSave);

        // Act
        List<DegreeDto> result = degreeService.findByOwnerUsername(ownerUsername);

        // Assert
        assertNotNull(result);
        assertEquals(1,result.size());
        assertNotNull(result.getFirst().getId());
        assertEquals(name,result.getFirst().getName());
        assertEquals(desc,result.getFirst().getDescription());
    }

    @Test
    void testFindImgUrlByDegreeIdReturnsImgUrl() {
        // Arrange
        Portfolio portfolioToSave = createPortfolio(userRepository);
        String name = "Generic Degree Title", desc = "Generic Degree Desc", imgUrl = "https://www.imgurl.com";
        Degree degreeToSave = new Degree(null,name,desc, LocalDate.now(),null,imgUrl,portfolioToSave);
        // Guardamos el Degree en el Repository para no depender del servicio para guardarlo (este test no trata de eso)
        portfolioToSave.setDegrees(Set.of(degreeToSave));

        Long degreeId = portfolioRepository.save(portfolioToSave).getDegrees().stream().toList().getFirst().getId();

        // Act
        String result = degreeService.findImgUrlByDegreeId(degreeId);

        // Assert
        assertNotNull(result);
        assertEquals(imgUrl,result);
    }

    @Test
    void testFindOwnerUsernameByDegreeIdReturnsOwnerUsername() {
        // Arrange
        Portfolio portfolioToSave = createPortfolio(userRepository);
        String name = "Generic Degree Title";
        String desc = "Generic Degree Desc";
        Degree degreeToSave = new Degree(null,name,desc, LocalDate.now(),null,null,portfolioToSave);
        // Guardamos el Degree en el Repository para no depender del servicio para guardarlo (este test no trata de eso)
        portfolioToSave.setDegrees(Set.of(degreeToSave));

        Long degreeId = portfolioRepository.save(portfolioToSave).getDegrees().stream().toList().getFirst().getId();

        // Act
        String result = degreeService.findOwnerUsernameByDegreeId(degreeId);

        // Assert
        assertNotNull(result);
        assertEquals(ownerUsername,result);
    }

    @Test
    void testDegreeCRUDLifeCycle(){
        // Arrange
        Portfolio portfolioToSave = createPortfolio(userRepository);

        // CREATE
        String name = "Generic Degree Title";
        String desc = "Generic Degree Desc";
        Degree degreeToSave = new Degree(null,name,desc, LocalDate.now(),null,null,portfolioToSave);

        portfolioToSave.addDegree(degreeToSave);
        portfolioRepository.save(portfolioToSave);
        Degree savedDegree = portfolioRepository.save(portfolioToSave).getDegrees().stream().toList().getFirst();

        assertNotNull(savedDegree);
        assertEquals(name,savedDegree.getName());
        assertEquals(desc,savedDegree.getDescription());

        Long degreeId = savedDegree.getId();

        // READ
        DegreeDto searchedDegreeDto = degreeService.findById(degreeId);

        assertNotNull(searchedDegreeDto);
        assertEquals(savedDegree.getId(),searchedDegreeDto.getId());
        assertEquals(name,searchedDegreeDto.getName());
        assertEquals(desc,searchedDegreeDto.getDescription());

        // UPDATE
        String newName = "New Degree Title", newDesc = "New Degree Desc";
        DegreeDto newDegreeDto = new DegreeDto(null,newName,newDesc,LocalDate.of(2010,7,7),null,null);

        DegreeDto updatedDegreeDto = degreeService.update(newDegreeDto,degreeId);

        assertNotNull(updatedDegreeDto);
        assertEquals(degreeId,updatedDegreeDto.getId());
        assertEquals(newName,updatedDegreeDto.getName());
        assertEquals(newDesc,updatedDegreeDto.getDescription());

        // DELETE
        entityManager.flush();
        entityManager.clear();
        assertDoesNotThrow(()->degreeService.deleteById(degreeId),"El método fallo y lanzó una excepción, debería haber terminado con éxito y silenciosamente");
        assertThrows(ResourceNotFoundException.class,()->degreeService.findById(degreeId));
    }
}