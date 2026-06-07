package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.RoleRequestDto;
import com.lite.thinking.app.application.dto.RoleResponseDto;
import com.lite.thinking.app.application.mapper.RoleMapper;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Role;
import com.lite.thinking.app.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public RoleResponseDto createRole(RoleRequestDto requestDto) {
        if (roleRepository.existsByName(requestDto.getName())) {
            throw new EntityAlreadyExistsException("El rol con nombre '" + requestDto.getName() + "' ya existe.");
        }
        Role role = RoleMapper.toDomain(requestDto);
        Role savedRole = roleRepository.save(role);
        return RoleMapper.toResponseDto(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El rol con ID " + id + " no fue encontrado."));
        return RoleMapper.toResponseDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("El rol con nombre '" + name + "' no fue encontrado."));
        return RoleMapper.toResponseDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleResponseDto updateRole(Long id, RoleRequestDto requestDto) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El rol con ID " + id + " no fue encontrado."));

        if (!existingRole.getName().equalsIgnoreCase(requestDto.getName()) &&
                roleRepository.existsByName(requestDto.getName())) {
            throw new EntityAlreadyExistsException("El rol con nombre '" + requestDto.getName() + "' ya existe.");
        }

        existingRole.setName(requestDto.getName());
        if (requestDto.getIsActive() != null) {
            existingRole.setActive(requestDto.getIsActive());
        }

        Role updatedRole = roleRepository.save(existingRole);
        return RoleMapper.toResponseDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("El rol con ID " + id + " no fue encontrado.");
        }
        roleRepository.deleteById(id);
    }
}
