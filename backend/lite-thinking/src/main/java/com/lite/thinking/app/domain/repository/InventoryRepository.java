package com.lite.thinking.app.domain.repository;

import com.lite.thinking.app.domain.model.Inventory;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    Inventory save(Inventory inventory);
    Optional<Inventory> findById(Long id);
    List<Inventory> findAll();
    List<Inventory> findByProductCode(String productCode);
    List<Inventory> findByCompanyNit(String companyNit);
    Optional<Inventory> findByProductCodeAndCompanyNit(String productCode, String companyNit);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByProductCodeAndCompanyNit(String productCode, String companyNit);
    long count();
}
