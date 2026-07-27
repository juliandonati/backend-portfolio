package com.juliandonati.backendPortafolio.security.service;

import com.juliandonati.backendPortafolio.exception.ResourceNotFoundException;
import com.juliandonati.backendPortafolio.security.domain.Role;
import com.juliandonati.backendPortafolio.security.exception.RoleAlreadyExistsException;
import com.juliandonati.backendPortafolio.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Role findById(Long id) throws ResourceNotFoundException {
        Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró un rol con el id: " + id));
        return role;
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) throws ResourceNotFoundException {
        Role role = roleRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("No se encontró el rol: " + name));
        return role;
    }

    @Override
    @Transactional
    public Role save(Role role) throws RoleAlreadyExistsException {
        if(roleRepository.findByName(role.getName()).isPresent())
            throw new RoleAlreadyExistsException("Ya existe un rol con el nombre: " + role.getName());
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        if(!roleRepository.existsById(id))
            throw new ResourceNotFoundException("No existe un rol con la id: " + id);

        roleRepository.deleteById(id);
    }
}
