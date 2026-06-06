package com.lite.thinking.app.application.mapper;

import com.lite.thinking.app.application.dto.CategoryRequestDto;
import com.lite.thinking.app.application.dto.CategoryResponseDto;
import com.lite.thinking.app.domain.model.Category;
import com.lite.thinking.app.infrastructure.persistence.entity.CategoryEntity;

public class CategoryMapper {

    public static Category toDomain(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static CategoryEntity toEntity(Category domain) {
        if (domain == null) {
            return null;
        }
        CategoryEntity entity = CategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .build();
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static Category toDomain(CategoryRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .build();
    }

    public static CategoryResponseDto toResponseDto(Category domain) {
        if (domain == null) {
            return null;
        }
        return CategoryResponseDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .isActive(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
