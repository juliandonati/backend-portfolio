package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.domain.Skill;
import com.juliandonati.backendPortafolio.dto.SkillDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.juliandonati.backendPortafolio.service.MiscTestUtilities.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

@Transactional
class SkillServiceTest {
    @Autowired
    private SkillService skillService;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private final String skillName1 = "Skill 1";
    private final String skillDesc1 = "Skill Desc. 1";
    private final String skillLevel1 = "Advanced";
    private final String skillImgUrl1 = "https://www.img.com";
    private final String skillCategory1 = "Coding";
    private final String skillName2 = "Skill Number Two";
    private final String skillDesc2 = "Skill Desc. Number Two";
    private final String skillLevel2 = "Decent";
    private final String skillImgUrl2 = "https://www.img.com";
    private final String skillCategory2 = "Socials";

    @Test
    void testFindSkillsByOwnerUsernameReturnsListOfSkills() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.addSkill(
                new Skill(null, skillName1, skillDesc1, skillLevel1, skillImgUrl1, skillCategory1, null)
        );
        portfolio.addSkill(
                new Skill(null, skillName2, skillDesc2, skillLevel2, skillImgUrl2, skillCategory2, null)
        );
        portfolioRepository.save(portfolio);

        // Act
        List<SkillDto> resultList = skillService.findSkillsByOwnerUsername(TEST_OWNER_USERNAME);
        SkillDto result1 = resultList.stream().filter(s -> s.getName().equals(skillName1)).findFirst()
                .orElseThrow(() -> new AssertionError("No se cargó correctamente la habilidad de prueba 1"));
        SkillDto result2 = resultList.stream().filter(s -> s.getName().equals(skillName2)).findFirst()
                .orElseThrow(() -> new AssertionError("No se cargó correctamente la habilidad de prueba 2"));

        // Assert
        assertAll("Validando los campos de la lista de SkillDto",
                () -> assertNotNull(resultList),
                () -> assertEquals(2, resultList.size()),
                () -> assertNotNull(result1.getId()),
                () -> assertEquals(skillName1, result1.getName()),
                () -> assertEquals(skillDesc1, result1.getDescription()),
                () -> assertEquals(skillLevel1, result1.getLevel()),
                () -> assertEquals(skillImgUrl1, result1.getImgUrl()),
                () -> assertEquals(skillCategory1, result1.getCategory()),
                () -> assertNotNull(result2.getId()),
                () -> assertEquals(skillName2, result2.getName()),
                () -> assertEquals(skillDesc2, result2.getDescription()),
                () -> assertEquals(skillLevel2, result2.getLevel()),
                () -> assertEquals(skillImgUrl2, result2.getImgUrl()),
                () -> assertEquals(skillCategory2, result2.getCategory())
        );
    }

    @Test
    void testFindOwnerUsernameBySkillIdReturnsOwnerUsername() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);

        portfolio.addSkill(
                new Skill(null, skillName1, skillDesc1, skillLevel1, skillImgUrl1, skillCategory1, null)
        );
        Long skillId = portfolioRepository.save(portfolio).getSkills().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No se cargó correctamente la habilidad de prueba"))
                .getId();

        // Act
        String result = skillService.findOwnerUsernameBySkillId(skillId);

        assertNotNull(result);
        assertEquals(TEST_OWNER_USERNAME, result);
    }

    @Test
    void testFindOptionalImgUrlBySkillIdReturnsOptionalImgUrl() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.addSkill(
                new Skill(null, skillName1, skillDesc1, skillLevel1, skillImgUrl1, skillCategory1, null)
        );
        Long skillId = portfolioRepository.save(portfolio).getSkills().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No se cargó correctamente la habilidad de prueba"))
                .getId();

        // Act
        Optional<String> result = skillService.findOptionalImgUrlBySkillId(skillId);

        assertTrue(result.isPresent());
        assertEquals(skillImgUrl1, result.get());
    }

    @Test
    void testSkillCRUDLifeCycle() {
        // Arrange
        Portfolio portfolio = createPortfolio(userRepository);
        portfolio.addSkill(
                new Skill(null, skillName1, skillDesc1, skillLevel1, skillImgUrl1, skillCategory1, null)
        );
        portfolio.addSkill(
                new Skill(null, skillName2, skillDesc2, skillLevel2, skillImgUrl2, skillCategory2, null)
        );

        // CREATE
        Set<Skill> savedSkills = portfolioRepository.save(portfolio).getSkills();
        if (savedSkills == null)
            throw new AssertionError("No se cargó correctamente ninguna habilidad de prueba");
        Skill savedSkill1 = savedSkills.stream().filter(s -> s.getName().equals(skillName1)).findFirst()
                .orElseThrow(() -> new AssertionError("No se cargó correctamente la habilidad de prueba 1"));

        Skill savedSkill2 = savedSkills.stream().filter(s -> s.getName().equals(skillName2)).findFirst()
                .orElseThrow(() -> new AssertionError("No se cargó correctamente la habilidad de prueba 2"));

        assertAll("Validando los campos de la lista de SkillDto",
                () -> assertNotNull(savedSkills),
                () -> assertEquals(2, savedSkills.size()),
                () -> assertNotNull(savedSkill1.getId()),
                () -> assertEquals(skillName1, savedSkill1.getName()),
                () -> assertEquals(skillDesc1, savedSkill1.getDescription()),
                () -> assertEquals(skillLevel1, savedSkill1.getLevel()),
                () -> assertEquals(skillImgUrl1, savedSkill1.getImgUrl()),
                () -> assertEquals(skillCategory1, savedSkill1.getCategory()),
                () -> assertNotNull(savedSkill2.getId()),
                () -> assertEquals(skillName2, savedSkill2.getName()),
                () -> assertEquals(skillDesc2, savedSkill2.getDescription()),
                () -> assertEquals(skillLevel2, savedSkill2.getLevel()),
                () -> assertEquals(skillImgUrl2, savedSkill2.getImgUrl()),
                () -> assertEquals(skillCategory2, savedSkill2.getCategory())
        );

        Long skillId1 = savedSkill1.getId();
        Long skillId2 = savedSkill2.getId();

        // READ
        SkillDto searchedSkillDto1 = skillService.findById(skillId1);
        SkillDto searchedSkillDto2 = skillService.findById(skillId2);

        assertAll("Validando los campos de la lista de SkillDto",
                () -> assertNotNull(searchedSkillDto1),
                () -> assertEquals(skillId1, searchedSkillDto1.getId()),
                () -> assertEquals(skillName1, searchedSkillDto1.getName()),
                () -> assertEquals(skillDesc1, searchedSkillDto1.getDescription()),
                () -> assertEquals(skillLevel1, searchedSkillDto1.getLevel()),
                () -> assertEquals(skillImgUrl1, searchedSkillDto1.getImgUrl()),
                () -> assertEquals(skillCategory1, searchedSkillDto1.getCategory()),
                () -> assertNotNull(searchedSkillDto2),
                () -> assertEquals(skillId2, searchedSkillDto2.getId()),
                () -> assertEquals(skillName2, searchedSkillDto2.getName()),
                () -> assertEquals(skillDesc2, searchedSkillDto2.getDescription()),
                () -> assertEquals(skillLevel2, searchedSkillDto2.getLevel()),
                () -> assertEquals(skillImgUrl2, searchedSkillDto2.getImgUrl()),
                () -> assertEquals(skillCategory2, searchedSkillDto2.getCategory())
        );

        // UPDATE
        String newSkillName1 = "New Skill Name One";
        String newSkillDesc1 = "New Skill Description One";
        String newSkillLevel1 = "New Skill Level One";
        String newSkillImgUrl1 = "https://www.newskillimgurlone.com";
        String newSkillCategory1 = "New Skill Category One";
        SkillDto newSkillDto1 = new SkillDto(null, newSkillName1, newSkillDesc1, newSkillLevel1, newSkillImgUrl1, newSkillCategory1);
        String newSkillName2 = "New Skill Name 2";
        String newSkillDesc2 = "New Skill Description 2";
        String newSkillLevel2 = "New Skill Level 2";
        String newSkillImgUrl2 = "https://www.newskillimgurl2.com";
        String newSkillCategory2 = "New Skill Category 2";
        SkillDto newSkillDto2 = new SkillDto(null, newSkillName2, newSkillDesc2, newSkillLevel2, newSkillImgUrl2, newSkillCategory2);

        SkillDto updatedSkillDto1 = skillService.update(newSkillDto1, skillId1);
        SkillDto updatedSkillDto2 = skillService.update(newSkillDto2, skillId2);

        assertAll("Validando los campos de la lista de SkillDto",
                () -> assertNotNull(updatedSkillDto1),
                () -> assertEquals(skillId1, updatedSkillDto1.getId()),
                () -> assertEquals(newSkillName1, updatedSkillDto1.getName()),
                () -> assertEquals(newSkillDesc1, updatedSkillDto1.getDescription()),
                () -> assertEquals(newSkillLevel1, updatedSkillDto1.getLevel()),
                () -> assertEquals(newSkillImgUrl1, updatedSkillDto1.getImgUrl()),
                () -> assertEquals(newSkillCategory1, updatedSkillDto1.getCategory()),
                () -> assertNotNull(updatedSkillDto2),
                () -> assertEquals(skillId2, updatedSkillDto2.getId()),
                () -> assertEquals(newSkillName2, updatedSkillDto2.getName()),
                () -> assertEquals(newSkillDesc2, updatedSkillDto2.getDescription()),
                () -> assertEquals(newSkillLevel2, updatedSkillDto2.getLevel()),
                () -> assertEquals(newSkillImgUrl2, updatedSkillDto2.getImgUrl()),
                () -> assertEquals(newSkillCategory2, updatedSkillDto2.getCategory())
        );

        // DELETE
        entityManager.clear();
        assertDoesNotThrow(() ->
        {
            skillService.deleteById(skillId1);
            entityManager.flush();
        }, TEST_THROWS_MESSAGE);
        assertThrows(ResourceNotFoundException.class, () -> skillService.findById(skillId1));
        assertDoesNotThrow(() -> {
            skillService.deleteById(skillId2);
            entityManager.flush();
        }, TEST_THROWS_MESSAGE);
        assertThrows(ResourceNotFoundException.class, () -> skillService.findById(skillId2));
    }
}