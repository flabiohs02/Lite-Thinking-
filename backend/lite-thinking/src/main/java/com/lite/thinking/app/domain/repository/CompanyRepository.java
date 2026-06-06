package com.lite.thinking.app.domain.repository;

import com.lite.thinking.app.domain.model.Company;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository {
    Company save(Company company);
    Optional<Company> findByNit(String nit);
    List<Company> findAll();
    void deleteByNit(String nit);
    boolean existsByNit(String nit);
}
