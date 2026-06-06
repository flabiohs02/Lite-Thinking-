package com.lite.thinking.app.application.mapper;

import com.lite.thinking.app.application.dto.UserRequestDto;
import com.lite.thinking.app.application.dto.UserResponseDto;
import com.lite.thinking.app.domain.model.User;
import com.lite.thinking.app.domain.model.Role;
import com.lite.thinking.app.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .identification(entity.getIdentification())
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .password(entity.getPassword())
                .role(RoleMapper.toDomain(entity.getRole()))
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        UserEntity entity = UserEntity.builder()
                .id(domain.getId())
                .identification(domain.getIdentification())
                .name(domain.getName())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .password(domain.getPassword())
                .role(RoleMapper.toEntity(domain.getRole()))
                .build();
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static User toDomain(UserRequestDto dto, Role role) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .identification(dto.getIdentification())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(dto.getPassword())
                .role(role)
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .build();
    }

    public static UserResponseDto toResponseDto(User domain) {
        if (domain == null) {
            return null;
        }
        return UserResponseDto.builder()
                .id(domain.getId())
                .identification(domain.getIdentification())
                .name(domain.getName())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .role(RoleMapper.toResponseDto(domain.getRole()))
                .isActive(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
