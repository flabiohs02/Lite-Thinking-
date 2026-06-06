package com.lite.thinking.app.infrastructure.persistence.adapter;

import com.lite.thinking.app.application.mapper.ProductMapper;
import com.lite.thinking.app.domain.model.Product;
import com.lite.thinking.app.domain.repository.ProductRepository;
import com.lite.thinking.app.infrastructure.persistence.entity.ProductEntity;
import com.lite.thinking.app.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductMapper.toEntity(product);
        ProductEntity savedEntity = productJpaRepository.save(entity);
        return ProductMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findByCode(String code) {
        return productJpaRepository.findById(code)
                .map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByCode(String code) {
        productJpaRepository.deleteById(code);
    }

    @Override
    public boolean existsByCode(String code) {
        return productJpaRepository.existsById(code);
    }

    @Override
    public List<Product> findByCompanyNit(String companyNit) {
        return productJpaRepository.findByCompanyNit(companyNit).stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }
}
