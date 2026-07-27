package com.juliandonati.backendPortafolio.security.service;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.dto.UserSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserSummaryResponseDto> findAll(String name, Pageable pageable);
    User findById(long id) throws ResourceNotFoundException;
    User findByEmail(String email) throws ResourceNotFoundException;
    User findByUsername(String username) throws ResourceNotFoundException;

    User save(User user) throws ResourceNotFoundException;
    User register(RegisterRequestDto registerRequestDto);

    void deleteById(Long id) throws ResourceNotFoundException;
    void deleteByEmail(String email) throws ResourceNotFoundException;
    void deleteByUsername(String username) throws ResourceNotFoundException;

    boolean hasPortfolio(String username) throws ResourceNotFoundException;

}
