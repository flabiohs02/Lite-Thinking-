package com.lite.thinking.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogProductResponseDto {
    private String code;
    private String name;
    private String characteristics;
    private String avatar;
    private List<ProductPriceDto> prices;
    private CompanyResponseDto company;
    private List<CategoryResponseDto> categories;
    private Integer stockTotal;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
