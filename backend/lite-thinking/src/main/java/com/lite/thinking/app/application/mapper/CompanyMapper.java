package com.lite.thinking.app.application.mapper;

import com.lite.thinking.app.application.dto.CompanyRequestDto;
import com.lite.thinking.app.application.dto.CompanyResponseDto;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.infrastructure.persistence.entity.CompanyEntity;

public class CompanyMapper {

    public static Company toDomain(CompanyEntity entity) {
        if (entity == null) {
            return null;
        }
        return Company.builder()
                .nit(entity.getNit())
                .name(entity.getName())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static CompanyEntity toEntity(Company domain) {
        if (domain == null) {
            return null;
        }
        CompanyEntity entity = CompanyEntity.builder()
                .nit(domain.getNit())
                .name(domain.getName())
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .build();
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static Company toDomain(CompanyRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return Company.builder()
                .nit(dto.getNit())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .build();
    }

    public static CompanyResponseDto toResponseDto(Company domain) {
        if (domain == null) {
            return null;
        }
        return CompanyResponseDto.builder()
                .nit(domain.getNit())
                .name(domain.getName())
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .isActive(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
