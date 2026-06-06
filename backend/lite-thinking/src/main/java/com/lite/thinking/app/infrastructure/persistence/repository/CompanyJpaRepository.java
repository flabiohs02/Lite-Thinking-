package com.lite.thinking.app.infrastructure.persistence.repository;

import com.lite.thinking.app.infrastructure.persistence.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, String> {
}
