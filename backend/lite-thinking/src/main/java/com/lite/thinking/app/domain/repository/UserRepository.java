package com.lite.thinking.app.domain.repository;

import com.lite.thinking.app.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByIdentification(String identification);
    List<User> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByIdentification(String identification);
    List<User> findByRoleId(Long roleId);
}
