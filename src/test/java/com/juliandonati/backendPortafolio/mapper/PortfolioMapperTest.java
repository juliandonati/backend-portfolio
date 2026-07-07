package com.juliandonati.backendPortafolio.mapper;

import com.juliandonati.backendPortafolio.domain.*;
import com.juliandonati.backendPortafolio.dto.*;
import com.juliandonati.backendPortafolio.security.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioMapperTest {
    @Spy
    PresentationMapper presentationMapper = Mappers.getMapper(PresentationMapper.class);
    @Spy
    AboutMeMapper aboutMeMapper = Mappers.getMapper(AboutMeMapper.class);
    @Spy
    DegreeMapper degreeMapper = Mappers.getMapper(DegreeMapper.class);
    @Spy
    JobMapper jobMapper = Mappers.getMapper(JobMapper.class);
    @Spy
    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @InjectMocks
    PortfolioMapperImpl portfolioMapper;

    @Test
    void testMapPortfolioEntityToPortfolioResponseDtoSuccessfully() {
        Portfolio mockPortfolio = new Portfolio();

        Long mockPresentationId = 2L;
        String mockPresentationName = "Carlos Larralde", mockPresentationTitle = "Data Analyst", mockPresentationDesc = "Love to code and do sum stuff like that!",
                mockPresentationImgUrl = "https://imagendeprueba.net", mockPresentationPhoneNumber="54332123543",
                mockPresentationEmail="carlos.larralde2002@yahoo.com.ar";
        Presentation mockPresentation = new Presentation(mockPresentationId,mockPresentationName,mockPresentationTitle,mockPresentationDesc,mockPresentationImgUrl,mockPresentationEmail,mockPresentationPhoneNumber,mockPortfolio);


        Long mockAboutMeId = 1L;
        String mockAboutMeTitle = "Sobre mí", mockAboutMeDesc = "Me encanta programar aplicaciones en Java.", mockAboutMeImgUrl = "http://bgimage.com",
                mockAboutMeBtnText = "Presiona aquí", mockAboutMeBtnUrl = "http://github.com";
        AboutMe mockAboutMe = new AboutMe(mockAboutMeId,mockAboutMeTitle,mockAboutMeDesc,mockAboutMeImgUrl,mockAboutMeBtnText,mockAboutMeBtnUrl,mockPortfolio);

        Long mockDegreeId = 1L;
        String mockDegreeName = "Ingeniería en Sistemas", mockDegreeDesc = "En la Universidad Pichango Tecno";
        LocalDate mockDegreeStartDate = LocalDate.now();
        String mockDegreeImgUrl = "http://www.degreeimg.com";
        Degree mockDegree = new Degree(mockDegreeId,mockDegreeName,mockDegreeDesc,mockDegreeStartDate,null,mockDegreeImgUrl,mockPortfolio);

        Long mockJobId = 7L;
        String mockJobName = "SoftwareDevelopers Inc", mockJobPosition = "Data Analyst", mockJobDesc = "Me encargué de cumplir mi trabajo responsablemente.";
        LocalDate mockJobStartDate = LocalDate.of(2023,10,9);
        Job mockJob = new Job(mockJobId,mockJobName,mockJobPosition,mockJobDesc,mockJobStartDate,null,mockPortfolio);

        Long mockSkillId = 14L;
        String mockSkillName = "Spring Boot", mockSkillDesc = "I know how to build enterprise apps with Spring Boot.",
                mockSkillLevel = "Intermediate", mockSkillImgUrl = "https://skillimage.com/springboot", mockSkillCategory= "Back-end Coding";
        Skill mockSkill = new Skill(mockSkillId,mockSkillName,mockSkillDesc,mockSkillLevel,mockSkillImgUrl,mockSkillCategory,mockPortfolio);

        mockPortfolio.setPresentation(mockPresentation);
        mockPortfolio.setAboutMe(mockAboutMe);
        mockPortfolio.setDegrees(Set.of(new Degree(5L,null,null,null,null,null,mockPortfolio),
                mockDegree, new Degree(9L,null,null,null,null,null,mockPortfolio)));
        mockPortfolio.setExperience(Set.of(mockJob, new Job(9L,null,null,null,null,null,mockPortfolio)));
        mockPortfolio.setSkills(Set.of(mockSkill));

        User mockUser = new User(1L,"usuario","1234","usuario","usuario@example.com", Set.of(), mockPortfolio,Set.of(mockPortfolio));
        mockPortfolio.setOwner(mockUser);

        PortfolioResponseDto result = portfolioMapper.toPortfolioResponseDto(mockPortfolio);
        PresentationDto resultPresentation = result.getPresentation();
        AboutMeDto resultAboutMe = result.getAboutMe();
        Set<DegreeDto> resultDegrees = result.getDegrees();
        Set<JobDto> resultExperience = result.getExperience();
        Set<SkillDto> resultSkills = result.getSkills();

        assertAll("Verificando los campos tras el mappeo...",
                ()->assertNotNull(result),
                ()->assertEquals(mockPresentationId,resultPresentation.getId()),
                ()->assertEquals(mockPresentationName,resultPresentation.getName()),
                ()->assertEquals(mockPresentationTitle,resultPresentation.getTitle()),
                ()->assertEquals(mockPresentationDesc,resultPresentation.getDescription()),
                ()->assertEquals(mockPresentationImgUrl,resultPresentation.getImgUrl()),
                ()->assertEquals(mockPresentationPhoneNumber,resultPresentation.getPhoneNumber()),
                ()->assertEquals(mockPresentationEmail,resultPresentation.getEmail()),
                ()->assertEquals(mockAboutMeId,resultAboutMe.getId()),
                ()->assertEquals(mockAboutMeTitle,resultAboutMe.getTitle()),
                ()->assertEquals(mockAboutMeDesc,resultAboutMe.getDescription()),
                ()->assertEquals(mockAboutMeImgUrl,resultAboutMe.getBgImgUrl()),
                ()->assertEquals(mockAboutMeBtnText,resultAboutMe.getButtonText()),
                ()->assertEquals(mockAboutMeBtnUrl,resultAboutMe.getButtonUrl()),
                ()->assertEquals(3,resultDegrees.size()),
                ()->assertEquals(2,resultExperience.size()),
                ()->assertEquals(1,resultSkills.size())
        );
        verify(presentationMapper,times(1)).toDto(mockPresentation);
        verify(aboutMeMapper,times(1)).toDto(mockAboutMe);
        verify(degreeMapper,times(3)).toDto(any(Degree.class));
        verify(jobMapper,times(2)).toDto(any(Job.class));
        verify(skillMapper,times(1)).toDto(mockSkill);
    }

    @Test
    void testMapUserEntityToStringSuccessfully() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("usuario");

        String result = portfolioMapper.mapUserToString(mockUser);

        assertNotNull(result);
        assertEquals("usuario",result);
        verify(mockUser,times(1)).getUsername();
    }
}