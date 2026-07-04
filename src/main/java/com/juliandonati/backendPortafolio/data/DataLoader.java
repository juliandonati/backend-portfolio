package com.juliandonati.backendPortafolio.data;

import com.juliandonati.backendPortafolio.domain.*;
import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.service.RoleService;
import com.juliandonati.backendPortafolio.security.service.UserService;
import com.juliandonati.backendPortafolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;
    private final PortfolioService portfolioService;

    @Value("${DEFAULT_ADMIN_PASSWORD}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) throws Exception {
        Role userRole;
        try{
            userRole = roleService.findByName("ROLE_USER");
        }
        catch(ResourceNotFoundException ex){
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("Rol genérico de usuario");
            roleService.save(userRole);
        }

        Role adminRole;
        try{
            adminRole = roleService.findByName("ROLE_ADMIN");
        }
        catch(ResourceNotFoundException ex){
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("Rol genérico de administrador");
            roleService.save(adminRole);
        }

        User userTestUser;
        try{
            userTestUser = userService.findByUsername("usuario");
        }
        catch(ResourceNotFoundException ex){
            RegisterRequestDto requestDto = new RegisterRequestDto();
            requestDto.setUsername("usuario");
            requestDto.setUnencryptedPassword("1234");
            requestDto.setRoles(Collections.singleton("ROLE_USER"));
            requestDto.setEmail("usuario@example.com");
            requestDto.setDisplayName("usuario test");

            userTestUser = userService.register(requestDto);
        }

        User adminTestUser;
        try{
            adminTestUser = userService.findByUsername("admin_test_user");
        }
        catch(ResourceNotFoundException ex){
            RegisterRequestDto requestDto = new RegisterRequestDto();
            requestDto.setUsername("admin");
            requestDto.setUnencryptedPassword(defaultAdminPassword);
            requestDto.setDisplayName("USER NAME");
            requestDto.setEmail("admin@example.com");

            Set<String> roles = new HashSet<>();
            roles.add("ROLE_ADMIN");
            roles.add("ROLE_USER");

            requestDto.setRoles(roles);

            adminTestUser = userService.register(requestDto);
        }

        Portfolio adminTestPortfolio;
        try{
            adminTestPortfolio = portfolioService.findByOwnerUsername(adminTestUser.getUsername());
        }
        catch (ResourceNotFoundException ex){
            adminTestPortfolio = new Portfolio();

            Presentation presentation = new Presentation(null,"Julián Donati","Ingeniero en Sistemas","Apasionado por la programación y por el café.","https://i.imgur.com/w5FBSjQ.jpeg","juliandonati5@gmail.com","5492236900433",adminTestPortfolio);
            adminTestPortfolio.setPresentation(presentation);

            AboutMe aboutMe = new AboutMe(null,"SOBRE MÍ","TEXTO SOBRE MÍ",null,null,null,adminTestPortfolio);
            adminTestPortfolio.setAboutMe(aboutMe);

            adminTestPortfolio.addDegree(new Degree(null,"TÍTULO ACADÉMICO 1","DESCRIPCIÓN DE TÍTULO ACADÉMICO", LocalDate.now(),null,null,adminTestPortfolio));

            adminTestPortfolio.addSkill(new Skill(null,"Java","Se programar sistemas complejos en Java, lorem ipsum...","Avanzado","https://i.imgur.com/kS5Id9I.png","Backend",adminTestPortfolio));

            adminTestPortfolio.addExperience(new Job(null,"TRABAJO 1","POSICIÓN TRABAJO 1","DESCRIPCIÓN TRABAJO 1",LocalDate.now(),null,adminTestPortfolio));

            adminTestPortfolio.addDegree(new Degree(null, "Ingeniería en Sistemas", "la mejor de todas",LocalDate.now(),null,null,adminTestPortfolio));

            adminTestPortfolio.setOwner(adminTestUser);
            adminTestUser.setOwnedPortfolio(adminTestPortfolio);

            userService.save(adminTestUser); // Se vuelve a guardar el usuario de prueba, solo que ahora se le agrega el portfolio.
        }
    }

}
