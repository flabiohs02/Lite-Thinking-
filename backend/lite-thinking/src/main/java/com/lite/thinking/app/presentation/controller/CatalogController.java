package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.CatalogProductResponseDto;
import com.lite.thinking.app.application.mapper.CategoryMapper;
import com.lite.thinking.app.application.mapper.CompanyMapper;
import com.lite.thinking.app.application.mapper.ProductMapper;
import com.lite.thinking.app.domain.model.Inventory;
import com.lite.thinking.app.domain.model.Product;
import com.lite.thinking.app.domain.repository.InventoryRepository;
import com.lite.thinking.app.domain.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Endpoints públicos del catálogo de tienda")
public class CatalogController {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @GetMapping("/products")
    @Operation(summary = "Obtener productos disponibles", description = "Retorna productos activos con stock total mayor que uno.")
    public ResponseEntity<List<CatalogProductResponseDto>> getAvailableProducts() {
        List<CatalogProductResponseDto> products = productRepository.findAll().stream()
                .filter(Product::isActive)
                .map(this::toCatalogProduct)
                .filter(product -> product.getStockTotal() != null && product.getStockTotal() > 1)
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    private CatalogProductResponseDto toCatalogProduct(Product product) {
        int stockTotal = inventoryRepository.findByProductCode(product.getCode()).stream()
                .filter(Inventory::isActive)
                .filter(inventory -> inventory.getStock() != null)
                .mapToInt(Inventory::getStock)
                .sum();

        return CatalogProductResponseDto.builder()
                .code(product.getCode())
                .name(product.getName())
                .characteristics(product.getCharacteristics())
                .avatar(product.getAvatar())
                .prices(product.getPrices().stream()
                        .map(ProductMapper::toPriceDto)
                        .collect(Collectors.toList()))
                .company(CompanyMapper.toResponseDto(product.getCompany()))
                .categories(product.getCategories().stream()
                        .map(CategoryMapper::toResponseDto)
                        .collect(Collectors.toList()))
                .stockTotal(stockTotal)
                .isActive(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
