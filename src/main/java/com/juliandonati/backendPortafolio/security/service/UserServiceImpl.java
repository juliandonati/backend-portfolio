package com.juliandonati.backendPortafolio.security.service;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.User;
import com.juliandonati.backendPortafolio.security.dto.RegisterRequestDto;
import com.juliandonati.backendPortafolio.security.dto.UserSummaryResponseDto;
import com.juliandonati.backendPortafolio.security.exception.UserAlreadyExistsException;
import com.juliandonati.backendPortafolio.security.mapper.UserMapper;
import com.juliandonati.backendPortafolio.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponseDto> findAll(String name, Pageable pageable) {
        Page<User> usersPage;

        if(name != null && !name.trim().isEmpty())
            usersPage = userRepository.findByNameContainingIgnoreCase(name,pageable);
        else
            usersPage = userRepository.findAll(pageable);

        List<UserSummaryResponseDto> userSummaryResponseDtoList = usersPage.getContent().stream()
                .map(userMapper::toUserSummaryResponseDto)
                .toList();

        return new PageImpl<>(userSummaryResponseDtoList, pageable, usersPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(long id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró un usuario de id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) throws ResourceNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("No se encontró un usuario con el email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("No se encontró un usuario con el nombre: " + username));
    }

    @Override
    public User save(User user) throws ResourceNotFoundException {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User register(RegisterRequestDto registerRequestDto) {
        if(userRepository.existsByUsername(registerRequestDto.getUsername()))
            throw new UserAlreadyExistsException("Ya existe un usuario con el nombre: " + registerRequestDto.getUsername());
        if(userRepository.existsByEmail(registerRequestDto.getEmail()))
            throw new UserAlreadyExistsException("Ya existe un usuario con el email: " + registerRequestDto.getEmail());

        User userToSave = userMapper.toEntity(registerRequestDto);

        return userRepository.save(userToSave);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if(!userRepository.existsById(id))
            throw new ResourceNotFoundException("No existe un usuario con la id: " + id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        if(!userRepository.existsByEmail(email))
            throw new ResourceNotFoundException("No existe un usuario con el email: " + email);
        userRepository.deleteByEmail(email);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        if(!userRepository.existsByUsername(username))
            throw new ResourceNotFoundException("No existe un usuario con el nombre: " + username);
        userRepository.deleteByUsername(username);
    }

    @Override
    public boolean hasPortfolio(String username) throws ResourceNotFoundException {
        if(!userRepository.existsByUsername(username))
            throw new ResourceNotFoundException("No existe un usuario con el nombre: " + username);
        return userRepository.hasPortfolioByUsername(username);
    }
}
