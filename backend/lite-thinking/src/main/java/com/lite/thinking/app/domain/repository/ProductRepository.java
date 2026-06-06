package com.lite.thinking.app.domain.repository;

import com.lite.thinking.app.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findByCode(String code);
    List<Product> findAll();
    void deleteByCode(String code);
    boolean existsByCode(String code);
    List<Product> findByCompanyNit(String companyNit);
}
