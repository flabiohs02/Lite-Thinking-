package com.lite.thinking.app.infrastructure.persistence.adapter;

import com.lite.thinking.app.application.mapper.InventoryMapper;
import com.lite.thinking.app.domain.model.Inventory;
import com.lite.thinking.app.domain.repository.InventoryRepository;
import com.lite.thinking.app.infrastructure.persistence.entity.InventoryEntity;
import com.lite.thinking.app.infrastructure.persistence.repository.InventoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InventoryPersistenceAdapter implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryPersistenceAdapter(InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = inventoryJpaRepository;
    }

    @Override
    public Inventory save(Inventory inventory) {
        InventoryEntity entity = InventoryMapper.toEntity(inventory);
        InventoryEntity savedEntity = inventoryJpaRepository.save(entity);
        return InventoryMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return inventoryJpaRepository.findById(id)
                .map(InventoryMapper::toDomain);
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryJpaRepository.findAll().stream()
                .map(InventoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findByProductCode(String productCode) {
        return inventoryJpaRepository.findByProductCode(productCode).stream()
                .map(InventoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findByCompanyNit(String companyNit) {
        return inventoryJpaRepository.findByCompanyNit(companyNit).stream()
                .map(InventoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Inventory> findByProductCodeAndCompanyNit(String productCode, String companyNit) {
        return inventoryJpaRepository.findByProductCodeAndCompanyNit(productCode, companyNit)
                .map(InventoryMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        inventoryJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return inventoryJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByProductCodeAndCompanyNit(String productCode, String companyNit) {
        return inventoryJpaRepository.existsByProductCodeAndCompanyNit(productCode, companyNit);
    }

    @Override
    public long count() {
        return inventoryJpaRepository.count();
    }
}
