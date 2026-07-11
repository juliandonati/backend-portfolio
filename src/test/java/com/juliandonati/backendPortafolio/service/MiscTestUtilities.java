package com.juliandonati.backendPortafolio.service;

import com.juliandonati.backendPortafolio.domain.Portfolio;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;

import java.util.Set;

public class MiscTestUtilities {
    public static final String TEST_OWNER_USERNAME = "usuarioTest";
    public static User createAndSaveUser(UserRepository userRepository){
        User user = new User(null,TEST_OWNER_USERNAME,"1234",TEST_OWNER_USERNAME,"email@ejemplo.com", Set.of(),null,Set.of());
        return userRepository.save(user);
    }
    public static Portfolio createPortfolio(UserRepository userRepository){
        User user = createAndSaveUser(userRepository);
        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(user);
        return portfolio;
    }


}
