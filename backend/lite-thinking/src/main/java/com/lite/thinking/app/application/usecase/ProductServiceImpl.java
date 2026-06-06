package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.ProductRequestDto;
import com.lite.thinking.app.application.dto.ProductResponseDto;
import com.lite.thinking.app.application.mapper.ProductMapper;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Product;
import com.lite.thinking.app.domain.model.ProductPrice;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.domain.model.Category;
import com.lite.thinking.app.domain.repository.CompanyRepository;
import com.lite.thinking.app.domain.repository.ProductRepository;
import com.lite.thinking.app.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        if (productRepository.existsByCode(requestDto.getCode())) {
            throw new EntityAlreadyExistsException("El producto con código " + requestDto.getCode() + " ya existe.");
        }

        Company company = companyRepository.findByNit(requestDto.getCompanyNit())
                .orElseThrow(() -> new EntityNotFoundException("La empresa con NIT " + requestDto.getCompanyNit() + " no existe. No se puede asociar el producto."));

        List<Category> categories = new ArrayList<>();
        if (requestDto.getCategoryIds() != null && !requestDto.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllByIds(requestDto.getCategoryIds());
            if (categories.size() != requestDto.getCategoryIds().size()) {
                throw new EntityNotFoundException("Una o más categorías especificadas no existen.");
            }
        }

        Product product = ProductMapper.toDomain(requestDto, company, categories);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toResponseDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("El producto con código " + code + " no fue encontrado."));
        return ProductMapper.toResponseDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(String code, ProductRequestDto requestDto) {
        Product existingProduct = productRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("El producto con código " + code + " no fue encontrado."));

        Company company = companyRepository.findByNit(requestDto.getCompanyNit())
                .orElseThrow(() -> new EntityNotFoundException("La empresa con NIT " + requestDto.getCompanyNit() + " no existe."));

        List<Category> categories = new ArrayList<>();
        if (requestDto.getCategoryIds() != null && !requestDto.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllByIds(requestDto.getCategoryIds());
            if (categories.size() != requestDto.getCategoryIds().size()) {
                throw new EntityNotFoundException("Una o más categorías especificadas no existen.");
            }
        }

        existingProduct.setName(requestDto.getName());
        existingProduct.setCharacteristics(requestDto.getCharacteristics());
        existingProduct.setAvatar(requestDto.getAvatar());
        existingProduct.setCompany(company);
        existingProduct.setCategories(categories);
        if (requestDto.getIsActive() != null) {
            existingProduct.setActive(requestDto.getIsActive());
        }

        List<ProductPrice> updatedPrices = requestDto.getPrices().stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
        existingProduct.setPrices(updatedPrices);

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toResponseDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(String code) {
        if (!productRepository.existsByCode(code)) {
            throw new EntityNotFoundException("El producto con código " + code + " no fue encontrado.");
        }
        productRepository.deleteByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCompanyNit(String companyNit) {
        if (!companyRepository.existsByNit(companyNit)) {
            throw new EntityNotFoundException("La empresa con NIT " + companyNit + " no fue encontrada.");
        }
        return productRepository.findByCompanyNit(companyNit).stream()
                .map(ProductMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
