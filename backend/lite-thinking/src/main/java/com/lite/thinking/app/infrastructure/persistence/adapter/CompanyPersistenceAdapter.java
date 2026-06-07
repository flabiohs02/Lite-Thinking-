package com.lite.thinking.app.infrastructure.persistence.adapter;

import com.lite.thinking.app.application.mapper.CompanyMapper;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.domain.repository.CompanyRepository;
import com.lite.thinking.app.infrastructure.persistence.entity.CompanyEntity;
import com.lite.thinking.app.infrastructure.persistence.repository.CompanyJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component

public class CompanyPersistenceAdapter implements CompanyRepository {

    private final CompanyJpaRepository companyJpaRepository;

    public CompanyPersistenceAdapter(CompanyJpaRepository companyJpaRepository) {
        this.companyJpaRepository = companyJpaRepository;
    }

    @Override
    public Company save(Company company) {
        CompanyEntity entity = CompanyMapper.toEntity(company);
        CompanyEntity savedEntity = companyJpaRepository.save(entity);
        return CompanyMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Company> findByNit(String nit) {
        return companyJpaRepository.findById(nit)
                .map(CompanyMapper::toDomain);
    }

    @Override
    public List<Company> findAll() {
        return companyJpaRepository.findAll().stream()
                .map(CompanyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByNit(String nit) {
        companyJpaRepository.deleteById(nit);
    }

    @Override
    public boolean existsByNit(String nit) {
        return companyJpaRepository.existsById(nit);
    }

    @Override
    public long count() {
        return companyJpaRepository.count();
    }
}
