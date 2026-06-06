package com.lite.thinking.app.application.mapper;

import com.lite.thinking.app.application.dto.ProductPriceDto;
import com.lite.thinking.app.application.dto.ProductRequestDto;
import com.lite.thinking.app.application.dto.ProductResponseDto;
import com.lite.thinking.app.application.dto.CategoryResponseDto;
import com.lite.thinking.app.domain.model.Product;
import com.lite.thinking.app.domain.model.ProductPrice;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.domain.model.Category;
import com.lite.thinking.app.infrastructure.persistence.entity.ProductEntity;
import com.lite.thinking.app.infrastructure.persistence.entity.ProductPriceEntity;
import com.lite.thinking.app.infrastructure.persistence.entity.CompanyEntity;
import com.lite.thinking.app.infrastructure.persistence.entity.CategoryEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductPrice toDomain(ProductPriceEntity entity) {
        if (entity == null) {
            return null;
        }
        return ProductPrice.builder()
                .currency(entity.getCurrency())
                .amount(entity.getAmount())
                .build();
    }

    public static ProductPriceEntity toEntity(ProductPrice domain) {
        if (domain == null) {
            return null;
        }
        return ProductPriceEntity.builder()
                .currency(domain.getCurrency())
                .amount(domain.getAmount())
                .build();
    }

    public static ProductPrice toDomain(ProductPriceDto dto) {
        if (dto == null) {
            return null;
        }
        return ProductPrice.builder()
                .currency(dto.getCurrency())
                .amount(dto.getAmount())
                .build();
    }

    public static ProductPriceDto toPriceDto(ProductPrice domain) {
        if (domain == null) {
            return null;
        }
        return ProductPriceDto.builder()
                .currency(domain.getCurrency())
                .amount(domain.getAmount())
                .build();
    }

    public static Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        List<ProductPrice> prices = entity.getPrices() == null ? Collections.emptyList() :
                entity.getPrices().stream().map(ProductMapper::toDomain).collect(Collectors.toList());

        List<Category> categories = entity.getCategories() == null ? Collections.emptyList() :
                entity.getCategories().stream().map(CategoryMapper::toDomain).collect(Collectors.toList());

        return Product.builder()
                .code(entity.getCode())
                .name(entity.getName())
                .characteristics(entity.getCharacteristics())
                .avatar(entity.getAvatar())
                .prices(prices)
                .company(CompanyMapper.toDomain(entity.getCompany()))
                .categories(categories)
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        List<ProductPriceEntity> priceEntities = domain.getPrices() == null ? new ArrayList<>() :
                domain.getPrices().stream().map(ProductMapper::toEntity).collect(Collectors.toList());

        CompanyEntity companyEntity = domain.getCompany() == null ? null : CompanyMapper.toEntity(domain.getCompany());

        List<CategoryEntity> categoryEntities = domain.getCategories() == null ? new ArrayList<>() :
                domain.getCategories().stream().map(CategoryMapper::toEntity).collect(Collectors.toList());

        ProductEntity entity = ProductEntity.builder()
                .code(domain.getCode())
                .name(domain.getName())
                .characteristics(domain.getCharacteristics())
                .avatar(domain.getAvatar())
                .prices(priceEntities)
                .company(companyEntity)
                .categories(categoryEntities)
                .build();
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static Product toDomain(ProductRequestDto dto, Company company, List<Category> categories) {
        if (dto == null) {
            return null;
        }
        List<ProductPrice> prices = dto.getPrices() == null ? Collections.emptyList() :
                dto.getPrices().stream().map(ProductMapper::toDomain).collect(Collectors.toList());

        return Product.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .characteristics(dto.getCharacteristics())
                .avatar(dto.getAvatar())
                .prices(prices)
                .company(company)
                .categories(categories)
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .build();
    }

    public static ProductResponseDto toResponseDto(Product domain) {
        if (domain == null) {
            return null;
        }
        List<ProductPriceDto> priceDtos = domain.getPrices() == null ? Collections.emptyList() :
                domain.getPrices().stream().map(ProductMapper::toPriceDto).collect(Collectors.toList());

        List<CategoryResponseDto> categoryDtos = domain.getCategories() == null ? Collections.emptyList() :
                domain.getCategories().stream().map(CategoryMapper::toResponseDto).collect(Collectors.toList());

        return ProductResponseDto.builder()
                .code(domain.getCode())
                .name(domain.getName())
                .characteristics(domain.getCharacteristics())
                .avatar(domain.getAvatar())
                .prices(priceDtos)
                .company(CompanyMapper.toResponseDto(domain.getCompany()))
                .categories(categoryDtos)
                .isActive(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
