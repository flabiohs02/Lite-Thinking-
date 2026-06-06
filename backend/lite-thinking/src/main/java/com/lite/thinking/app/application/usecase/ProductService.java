package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.ProductRequestDto;
import com.lite.thinking.app.application.dto.ProductResponseDto;
import java.util.List;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto requestDto);
    ProductResponseDto getProductByCode(String code);
    List<ProductResponseDto> getAllProducts();
    ProductResponseDto updateProduct(String code, ProductRequestDto requestDto);
    void deleteProduct(String code);
    List<ProductResponseDto> getProductsByCompanyNit(String companyNit);
}
