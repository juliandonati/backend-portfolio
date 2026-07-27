package com.juliandonati.backendPortafolio.security.service;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.exception.RoleAlreadyExistsException;

import java.util.List;

public interface RoleService {
    List<Role> findAll();
    Role findById(Long id);
    Role findByName(String name);
    Role save(Role role) throws RoleAlreadyExistsException;
    void deleteById(Long id) throws ResourceNotFoundException;
}
