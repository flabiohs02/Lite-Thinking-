package com.lite.thinking.app.infrastructure.persistence.repository;

import com.lite.thinking.app.infrastructure.persistence.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {
    List<InventoryEntity> findByProductCode(String productCode);
    List<InventoryEntity> findByCompanyNit(String companyNit);
    Optional<InventoryEntity> findByProductCodeAndCompanyNit(String productCode, String companyNit);
    boolean existsByProductCodeAndCompanyNit(String productCode, String companyNit);
}
