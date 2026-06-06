package com.lite.thinking.app.infrastructure.persistence.adapter;

import com.lite.thinking.app.application.mapper.UserMapper;
import com.lite.thinking.app.domain.model.User;
import com.lite.thinking.app.domain.repository.UserRepository;
import com.lite.thinking.app.infrastructure.persistence.entity.UserEntity;
import com.lite.thinking.app.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByIdentification(String identification) {
        return userJpaRepository.findByIdentification(identification)
                .map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByIdentification(String identification) {
        return userJpaRepository.existsByIdentification(identification);
    }

    @Override
    public List<User> findByRoleId(Long roleId) {
        return userJpaRepository.findByRoleId(roleId).stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }
}
