package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Job;
import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.dto.JobDto;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.repository.PortfolioRepository;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class JobServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private JobService jobService;


    @Test
    void testFindJobByOwnerUsernameReturnsListOfJobs() {
        // Arrange
        String ownerUsername = "usuarioTest";
        User user = new User(null, ownerUsername, "1234", "usuarioDisplay", "usuario@ejemplo.com", Set.of(), null, Set.of());
        userRepository.save(user);
        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(user);

        String jobName1 = "Google";
        String jobName2 = "Apple";
        String jobPos1 = "Data Analyst";
        String jobPos2 = "Clerk";
        String jobDesc1 = "desc. placeholder 1";
        String jobDesc2 = "desc. placeholder 2";
        LocalDate jobSDate2 =  LocalDate.of(2010, 12, 10), jobEDate2 = LocalDate.of(2012, 6, 1);
        Job
                job1 = new Job(null, jobName1, jobPos1, jobDesc1, LocalDate.now(), null, null),
                job2 = new Job(null, jobName2, jobPos2, jobDesc2,jobSDate2,jobEDate2, null);
        portfolio.addExperience(job1);
        portfolio.addExperience(job2);
        portfolioRepository.save(portfolio);

        // Act
        List<JobDto> result = jobService.findByOwnerUsername(ownerUsername);
        JobDto jobDto1 = result.stream().filter(jobDto -> jobDto.getName().equals(jobName1)).findFirst().orElseThrow(()->new AssertionError("No se encontró el trabajo "+jobName1+" en la Base de Datos")),
                jobDto2 = result.stream().filter(jobDto -> jobDto.getName().equals(jobName2)).findFirst().orElseThrow(()->new AssertionError("No se encontró el trabajo "+jobName2+" en la Base de Datos"));

        // Assert
        assertAll("Validando los campos de los JobDto...",
                () -> assertEquals(2, result.size()),
                () -> assertNotNull(jobDto1.getId()),
                () -> assertNotNull(jobDto2.getId()),
                () -> assertEquals(jobName1, jobDto1.getName()),
                () -> assertEquals(jobName2, jobDto2.getName()),
                () -> assertEquals(jobPos1, jobDto1.getPosition()),
                () -> assertEquals(jobPos2, jobDto2.getPosition()),
                () -> assertEquals(jobDesc1, jobDto1.getDescription()),
                () -> assertEquals(jobDesc2, jobDto2.getDescription()),
                () -> assertEquals(jobSDate2, jobDto2.getStartDate()),
                () -> assertEquals(jobEDate2, jobDto2.getEndDate())
        );
    }

    @Test
    void testDeleteJobByIdSuccessfully() {
        // Arrange
        User user = new User(null, "usuarioTest", "1234", "usuarioDisplay", "usuario@ejemplo.com", Set.of(), null, Set.of());
        userRepository.save(user);
        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(user);

        Job job = new Job(null,"Trabajo","Posición","Descripción de trabajo",LocalDate.now(),null,null);
        portfolio.addExperience(job);
        Optional<Job> savedJob = portfolioRepository.save(portfolio).getExperience().stream().findFirst();

        Long jobId;
        jobId = savedJob.map(Job::getId).orElse(null);


        // Act + Assert
        assertNotNull(jobId,"No se guardó exitosamente el trabajo creado para el test.");
        assertDoesNotThrow(()->jobService.deleteById(jobId),"El método falló y lanzó una excepción, debería haber finalizado con éxito y silenciosamente");
        assertThrows(ResourceNotFoundException.class,()->jobService.findById(jobId));
    }

    @Test
    void testJobCRUDLifeCycle() {
        // Arrange
        User user = new User(null, "usuarioTest", "1234", "usuarioDisplay", "usuario@ejemplo.com", Set.of(), null, Set.of());
        userRepository.save(user);
        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(user);

        // CREATE
        String jobName1 = "Google";
        String jobName2 = "Apple";
        String jobPos1 = "Data Analyst";
        String jobPos2 = "Clerk";
        String jobDesc1 = "desc. placeholder 1";
        String jobDesc2 = "desc. placeholder 2";
        LocalDate jobSDate2 =  LocalDate.of(2010, 12, 10), jobEDate2 = LocalDate.of(2012, 6, 1);
        Job
                job1 = new Job(null, jobName1, jobPos1, jobDesc1, LocalDate.now(), null, null),
                job2 = new Job(null, jobName2, jobPos2, jobDesc2,jobSDate2,jobEDate2, null);

        portfolio.addExperience(job1);
        portfolio.addExperience(job2);
        List<Job> savedJobs = portfolioRepository.save(portfolio).getExperience().stream().toList();
        Job savedJob1 = savedJobs.stream().filter(job -> job.getName().equals(jobName1)).findFirst().orElse(null),
                savedJob2 = savedJobs.stream().filter(job -> job.getName().equals(jobName2)).findFirst().orElse(null);
        Long jobId1, jobId2;
        if(savedJob1 != null)
            jobId1 = savedJob1.getId();
        else
            jobId1 = null;
        if(savedJob2 != null)
            jobId2 = savedJob2.getId();
        else
            jobId2 = null;

        assertNotNull(savedJobs,"No se guardó ningún trabajo");
        assertNotNull(savedJob1,"No se guardó el trabajo 1");
        assertNotNull(savedJob2,"No se guardó el trabajo 2");
        assertAll("Validando los campos de los Job...",
                () -> assertEquals(2, savedJobs.size()),
                () -> assertNotNull(jobId1),
                () -> assertNotNull(jobId2),
                () -> assertEquals(jobName1, savedJob1.getName()),
                () -> assertEquals(jobName2, savedJob2.getName()),
                () -> assertEquals(jobPos1, savedJob1.getPosition()),
                () -> assertEquals(jobPos2, savedJob2.getPosition()),
                () -> assertEquals(jobDesc1, savedJob1.getDescription()),
                () -> assertEquals(jobDesc2, savedJob2.getDescription()),
                () -> assertEquals(jobSDate2, savedJob2.getStartDate()),
                () -> assertEquals(jobEDate2, savedJob2.getEndDate())
        );

        // READ
        JobDto searchedJob1 = jobService.findById(jobId1)
                , searchedJob2 = jobService.findById(jobId2);

        assertAll("Validando los campos del Job...",
                () -> assertNotNull(searchedJob1),
                () -> assertEquals(jobId1,searchedJob1.getId()),
                () -> assertEquals(jobName1, searchedJob1.getName()),
                () -> assertEquals(jobPos1, searchedJob1.getPosition()),
                () -> assertEquals(jobDesc1, searchedJob1.getDescription()),
                () -> assertNotNull(searchedJob2),
                () -> assertEquals(jobId2,searchedJob2.getId()),
                () -> assertEquals(jobName2, searchedJob2.getName()),
                () -> assertEquals(jobPos2, searchedJob2.getPosition()),
                () -> assertEquals(jobDesc2, searchedJob2.getDescription()),
                () -> assertEquals(jobSDate2, searchedJob2.getStartDate()),
                () -> assertEquals(jobEDate2, searchedJob2.getEndDate())
        );

        // UPDATE
        String newJobName2 = "Xiaomi";
        String newJobPos2 = "Manager";
        String newJobDesc2 = "updated job desc.";
        LocalDate newJobSDate2 = LocalDate.now();
        JobDto newJob2 = new JobDto(null,newJobName2,newJobPos2,newJobDesc2,newJobSDate2,null);

        JobDto updatedJob2 = jobService.update(newJob2,jobId2);

        assertAll("Validando los campos del Job...",
                () -> assertNotNull(updatedJob2),
                () -> assertEquals(jobId2,updatedJob2.getId()),
                () -> assertEquals(newJobName2, updatedJob2.getName()),
                () -> assertEquals(newJobPos2, updatedJob2.getPosition()),
                () -> assertEquals(newJobDesc2, updatedJob2.getDescription()),
                () -> assertEquals(newJobSDate2, updatedJob2.getStartDate()),
                () -> assertNull(updatedJob2.getEndDate())
        );
        // DELETE
        assertDoesNotThrow(()->jobService.deleteById(jobId1),"El método falló y lanzó una excepción, debería haber finalizado con éxito y silenciosamente");
        assertThrows(ResourceNotFoundException.class,()->jobService.findById(jobId1));
        assertDoesNotThrow(()->jobService.deleteById(jobId2),"El método falló y lanzó una excepción, debería haber finalizado con éxito y silenciosamente");
        assertThrows(ResourceNotFoundException.class,()->jobService.findById(jobId2));
    }

}