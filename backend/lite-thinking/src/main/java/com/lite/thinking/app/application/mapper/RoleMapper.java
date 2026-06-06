package com.lite.thinking.app.application.mapper;

import com.lite.thinking.app.application.dto.RoleRequestDto;
import com.lite.thinking.app.application.dto.RoleResponseDto;
import com.lite.thinking.app.domain.model.Role;
import com.lite.thinking.app.infrastructure.persistence.entity.RoleEntity;

public class RoleMapper {

    public static Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static RoleEntity toEntity(Role domain) {
        if (domain == null) {
            return null;
        }
        RoleEntity entity = RoleEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static Role toDomain(RoleRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return Role.builder()
                .name(dto.getName())
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .build();
    }

    public static RoleResponseDto toResponseDto(Role domain) {
        if (domain == null) {
            return null;
        }
        return RoleResponseDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .isActive(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
