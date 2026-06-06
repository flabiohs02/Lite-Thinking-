package com.lite.thinking.app.infrastructure.config;

import com.lite.thinking.app.infrastructure.persistence.entity.RoleEntity;
import com.lite.thinking.app.infrastructure.persistence.entity.UserEntity;
import com.lite.thinking.app.infrastructure.persistence.repository.RoleJpaRepository;
import com.lite.thinking.app.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleJpaRepository roleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        RoleEntity adminRole;
        Optional<RoleEntity> existingRole = roleJpaRepository.findById(1L);
        if (existingRole.isPresent()) {
            adminRole = existingRole.get();
        } else {
            Optional<RoleEntity> roleByName = roleJpaRepository.findByName("ADMIN");
            if (roleByName.isPresent()) {
                adminRole = roleByName.get();
            } else {
                RoleEntity newRole = RoleEntity.builder()
                        .name("ADMIN")
                        .build();
                newRole.setActive(true);
                adminRole = roleJpaRepository.save(newRole);
            }
        }

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
    }
}
