package com.lite.thinking.app.infrastructure.persistence.adapter;

import com.lite.thinking.app.application.mapper.RoleMapper;
import com.lite.thinking.app.domain.model.Role;
import com.lite.thinking.app.domain.repository.RoleRepository;
import com.lite.thinking.app.infrastructure.persistence.entity.RoleEntity;
import com.lite.thinking.app.infrastructure.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;

    @Override
    public Role save(Role role) {
        RoleEntity entity = RoleMapper.toEntity(role);
        RoleEntity savedEntity = roleJpaRepository.save(entity);
        return RoleMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id)
                .map(RoleMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name)
                .map(RoleMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(RoleMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        roleJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return roleJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return roleJpaRepository.existsByName(name);
    }
}
