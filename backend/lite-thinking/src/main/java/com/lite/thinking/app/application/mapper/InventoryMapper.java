package com.lite.thinking.app.application.mapper;

import com.lite.thinking.app.application.dto.InventoryRequestDto;
import com.lite.thinking.app.application.dto.InventoryResponseDto;
import com.lite.thinking.app.domain.model.Inventory;
import com.lite.thinking.app.domain.model.Product;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.infrastructure.persistence.entity.InventoryEntity;

public class InventoryMapper {

    public static Inventory toDomain(InventoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return Inventory.builder()
                .id(entity.getId())
                .product(ProductMapper.toDomain(entity.getProduct()))
                .company(CompanyMapper.toDomain(entity.getCompany()))
                .stock(entity.getStock())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static InventoryEntity toEntity(Inventory domain) {
        if (domain == null) {
            return null;
        }
        InventoryEntity entity = InventoryEntity.builder()
                .id(domain.getId())
                .product(ProductMapper.toEntity(domain.getProduct()))
                .company(CompanyMapper.toEntity(domain.getCompany()))
                .stock(domain.getStock())
                .build();
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static Inventory toDomain(InventoryRequestDto dto, Product product, Company company) {
        if (dto == null) {
            return null;
        }
        return Inventory.builder()
                .product(product)
                .company(company)
                .stock(dto.getStock())
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .build();
    }

    public static InventoryResponseDto toResponseDto(Inventory domain) {
        if (domain == null) {
            return null;
        }
        return InventoryResponseDto.builder()
                .id(domain.getId())
                .product(ProductMapper.toResponseDto(domain.getProduct()))
                .company(CompanyMapper.toResponseDto(domain.getCompany()))
                .stock(domain.getStock())
                .isActive(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
