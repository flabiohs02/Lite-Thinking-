package com.lite.thinking.app.infrastructure.persistence.repository;

import com.lite.thinking.app.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {
    List<ProductEntity> findByCompanyNit(String companyNit);
}
