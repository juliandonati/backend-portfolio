package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Skill;
import com.juliandonati.backendPortafolio.dto.SkillDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.mapper.SkillMapper;
import com.juliandonati.backendPortafolio.mapper.SkillMapperImpl;
import com.juliandonati.backendPortafolio.repository.SkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {
    @Mock
    SkillRepository skillRepository;
    @Spy
    SkillMapper skillMapper = new SkillMapperImpl();

    @InjectMocks
    SkillServiceImpl skillService;

    @Test
    void testFindAllSkillsReturnsListOfSkills() {
        Long mockId1 = 6L, mockId2 = 8L;
        String mockName1 = "Java", mockName2 = "Python";
        List<Skill> mockSkills = List.of(
                new Skill(mockId1, mockName1, null, null, null, null, null),
                new Skill(mockId2, mockName2, null, null, null, null, null)
        );
        when(skillRepository.findAll()).thenReturn(mockSkills);

        List<SkillDto> result = skillService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockId1, result.getFirst().getId());
        assertEquals(mockName1, result.getFirst().getName());
        assertEquals(mockId2, result.get(1).getId());
        assertEquals(mockName2, result.get(1).getName());
        verify(skillRepository, times(1)).findAll();
        verify(skillMapper, times(2)).toDto(any(Skill.class));
    }

    @Test
    void testFindSkillByIdReturnsSkill() {
        Long mockId = 6L;
        String mockName = "Java";
        Skill mockSkill = new Skill(mockId, mockName, null, null, null, null, null);
        when(skillRepository.findById(mockId)).thenReturn(Optional.of(mockSkill));

        SkillDto result = skillService.findById(mockId);

        assertNotNull(result);
        assertEquals(mockId,result.getId());
        assertEquals(mockName,result.getName());
        verify(skillRepository,times(1)).findById(mockId);
        verify(skillMapper,times(1)).toDto(mockSkill);
    }

    @Test
    void testFindSkillByIdThrowsResourceNotFoundException() {
        Long mockId = 999L;
        when(skillRepository.findById(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->skillService.findById(mockId));
        verify(skillRepository,times(1)).findById(mockId);
        verify(skillMapper,never()).toDto(any(Skill.class));
    }

    @Test
    void testSaveSkillSavesSkillSuccessfully() {
        String mockName = "JavaScript";
        Skill mockSavedSkill = new Skill(1L,mockName,null,null,null,null,null);
        when(skillRepository.save(any(Skill.class))).thenReturn(mockSavedSkill);

        SkillDto mockSkillToSave = new SkillDto(null,mockName,null,null,null,null);
        SkillDto result = skillService.save(mockSkillToSave);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(mockName,result.getName());
        verify(skillMapper,times(1)).toEntity(mockSkillToSave);
        verify(skillRepository,times(1)).save(any(Skill.class));
        verify(skillMapper,times(1)).toDto(mockSavedSkill);
    }

    @Test
    void testUpdateSkillUpdatesSkillSuccessfully() {
        Long mockId = 1L;
        String mockOldName = "JavaScript",
        mockNewName = "TypeScript";
        SkillDto mockNewSkillDto = new SkillDto(mockId,mockNewName,null,null,null,null);
        Skill mockOldSkill = new Skill(mockId,mockOldName,null,null,null,null,null),
        mockUpdatedSkill = new Skill(mockId,mockNewName,null,null,null,null,null);
        when(skillRepository.findById(mockId)).thenReturn(Optional.of(mockOldSkill));
        when(skillRepository.save(any(Skill.class))).thenReturn(mockUpdatedSkill);

        SkillDto result = skillService.update(mockNewSkillDto,mockId);

        assertNotNull(result);
        assertEquals(mockId,result.getId());
        assertEquals(mockNewName,result.getName());
        verify(skillMapper,times(1)).updateEntity(mockNewSkillDto,mockOldSkill);
        verify(skillRepository,times(1)).save(any(Skill.class));
        verify(skillMapper,times(1)).toDto(mockUpdatedSkill);
    }

    @Test
    void testUpdateSkillThrowsResourceNotFoundException() {
        Long mockId = 999L;
        when(skillRepository.findById(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->skillService.update(new SkillDto(),mockId));
        verify(skillRepository,times(1)).findById(mockId);
        verify(skillMapper,never()).updateEntity(any(SkillDto.class),any(Skill.class));
        verify(skillRepository,never()).save(any(Skill.class));
        verify(skillMapper,never()).toDto(any(Skill.class));
    }

    @Test
    void testDeleteSkillByIdDeletesSkillSuccessfully() {
        Long mockId = 1L;
        when(skillRepository.existsById(mockId)).thenReturn(true);

        assertDoesNotThrow(()->skillService.deleteById(mockId),"El método falló y lanzó una excepción, debería haber finalizado con éxito silenciosamente");
        verify(skillRepository,times(1)).existsById(mockId);
        verify(skillRepository,times(1)).deleteById(mockId);
    }

    @Test
    void testDeleteSkillByIdThrowsResourceNotFoundException() {
        Long mockId = 999L;
        when(skillRepository.existsById(mockId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,()->skillService.deleteById(mockId));
        verify(skillRepository,times(1)).existsById(mockId);
        verify(skillRepository,never()).deleteById(anyLong());
    }

    @Test
    void testFindSkillsByOwnerUsernameReturnsListOfSkills() {
        String ownerUsername = "pedrocatalan";
        Long mockId1 = 6L, mockId2 = 8L;
        String mockName1 = "Java", mockName2 = "Python";
        List<Skill> mockSkills = List.of(
                new Skill(mockId1, mockName1, null, null, null, null, null),
                new Skill(mockId2, mockName2, null, null, null, null, null)
        );
        when(skillRepository.findByOwnerUsername(ownerUsername)).thenReturn(mockSkills);

        List<SkillDto> result = skillService.findSkillsByOwnerUsername(ownerUsername);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockId1, result.getFirst().getId());
        assertEquals(mockName1, result.getFirst().getName());
        assertEquals(mockId2, result.get(1).getId());
        assertEquals(mockName2, result.get(1).getName());
        verify(skillRepository, times(1)).findByOwnerUsername(ownerUsername);
        verify(skillMapper, times(2)).toDto(any(Skill.class));
    }

    @Test
    void testFindOwnerUsernameBySkillIdReturnsOwnerUsername() {
        Long mockId = 6L;
        String ownerUsername = "pedrocatalan";
        when(skillRepository.findOwnerUsernameBySkillId(mockId)).thenReturn(Optional.of(ownerUsername));

        String result = skillService.findOwnerUsernameBySkillId(mockId);

        assertNotNull(result);
        assertEquals(ownerUsername,result);
        verify(skillRepository,times(1)).findOwnerUsernameBySkillId(mockId);
    }

    @Test
    void testFindOwnerUsernameBySkillIdThrowsResourceNotFoundException() {
        Long mockId = 999L;
        when(skillRepository.findOwnerUsernameBySkillId(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->skillService.findOwnerUsernameBySkillId(mockId));
        verify(skillRepository,times(1)).findOwnerUsernameBySkillId(mockId);
    }

    @Test
    void testFindImgUrlBySkillIdReturnsImgUrl() {
        String mockImgUrl = "http://imagenepica.com.ar";
        Long mockId= 45L;
        when(skillRepository.findImgUrlBySkillId(mockId)).thenReturn(Optional.of(mockImgUrl));

        String result = skillService.findImgUrlBySkillId(mockId);

        assertNotNull(result);
        assertEquals(mockImgUrl,result);
        verify(skillRepository,times(1)).findImgUrlBySkillId(mockId);
    }

    @Test
    void testFindImgUrlBySkillIdThrowsResourceNotFoundException() {
        Long mockId= 999L;
        when(skillRepository.findImgUrlBySkillId(mockId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->skillService.findImgUrlBySkillId(mockId));
        verify(skillRepository,times(1)).findImgUrlBySkillId(mockId);
    }
}