package com.lite.thinking.app.infrastructure.config;

import com.lite.thinking.app.infrastructure.persistence.entity.RoleEntity;
import com.lite.thinking.app.infrastructure.persistence.entity.UserEntity;
import com.lite.thinking.app.infrastructure.persistence.repository.RoleJpaRepository;
import com.lite.thinking.app.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component

public class DataInitializer implements CommandLineRunner {

    private final RoleJpaRepository roleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleJpaRepository roleJpaRepository, UserJpaRepository userJpaRepository,
            PasswordEncoder passwordEncoder) {
        this.roleJpaRepository = roleJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        RoleEntity adminRole = findOrCreateRole("ADMIN");
        RoleEntity externalRole = findOrCreateRole("EXTERNAL");

        // 2. Seed Admin User
        if (!userJpaRepository.existsByIdentification("admin")) {
            UserEntity adminUser = UserEntity.builder()
                    .identification("admin")
                    .name("Admin")
                    .email("admin@litethinking.com")
                    .phone("12345678")
                    .password(passwordEncoder.encode("12345678"))
                    .role(adminRole)
                    .build();
            adminUser.setActive(true);
            userJpaRepository.save(adminUser);
        }

        // 3. Seed External User
        if (!userJpaRepository.existsByIdentification("external")) {
            UserEntity externalUser = UserEntity.builder()
                    .identification("external")
                    .name("Usuario Externo")
                    .email("external@litethinking.com")
                    .phone("87654321")
                    .password(passwordEncoder.encode("12345678"))
                    .role(externalRole)
                    .build();
            externalUser.setActive(true);
            userJpaRepository.save(externalUser);
        }
    }

    private RoleEntity findOrCreateRole(String name) {
        Optional<RoleEntity> existing = roleJpaRepository.findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }
        RoleEntity newRole = RoleEntity.builder()
                .name(name)
                .build();
        newRole.setActive(true);
        return roleJpaRepository.save(newRole);
    }
}
