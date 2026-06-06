package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.InventoryRequestDto;
import com.lite.thinking.app.application.dto.InventoryResponseDto;
import com.lite.thinking.app.application.mapper.InventoryMapper;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Inventory;
import com.lite.thinking.app.domain.model.Product;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.domain.repository.InventoryRepository;
import com.lite.thinking.app.domain.repository.ProductRepository;
import com.lite.thinking.app.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public InventoryResponseDto createInventory(InventoryRequestDto requestDto) {
        if (inventoryRepository.existsByProductCodeAndCompanyNit(requestDto.getProductCode(), requestDto.getCompanyNit())) {
            throw new EntityAlreadyExistsException("El registro de inventario para el producto con código '" +
                    requestDto.getProductCode() + "' en la empresa con NIT '" + requestDto.getCompanyNit() + "' ya existe.");
        }

        Product product = productRepository.findByCode(requestDto.getProductCode())
                .orElseThrow(() -> new EntityNotFoundException("El producto con código " + requestDto.getProductCode() + " no existe."));

        Company company = companyRepository.findByNit(requestDto.getCompanyNit())
                .orElseThrow(() -> new EntityNotFoundException("La empresa con NIT " + requestDto.getCompanyNit() + " no existe."));

        Inventory inventory = InventoryMapper.toDomain(requestDto, product, company);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryMapper.toResponseDto(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDto getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El registro de inventario con ID " + id + " no fue encontrado."));
        return InventoryMapper.toResponseDto(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDto getInventoryByProductAndCompany(String productCode, String companyNit) {
        Inventory inventory = inventoryRepository.findByProductCodeAndCompanyNit(productCode, companyNit)
                .orElseThrow(() -> new EntityNotFoundException("El registro de inventario para el producto '" + productCode +
                        "' en la empresa '" + companyNit + "' no fue encontrado."));
        return InventoryMapper.toResponseDto(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(InventoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getInventoriesByProduct(String productCode) {
        if (!productRepository.existsByCode(productCode)) {
            throw new EntityNotFoundException("El producto con código " + productCode + " no existe.");
        }
        return inventoryRepository.findByProductCode(productCode).stream()
                .map(InventoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getInventoriesByCompany(String companyNit) {
        if (!companyRepository.existsByNit(companyNit)) {
            throw new EntityNotFoundException("La empresa con NIT " + companyNit + " no existe.");
        }
        return inventoryRepository.findByCompanyNit(companyNit).stream()
                .map(InventoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryResponseDto updateInventory(Long id, InventoryRequestDto requestDto) {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El registro de inventario con ID " + id + " no fue encontrado."));

        if (!existingInventory.getProduct().getCode().equalsIgnoreCase(requestDto.getProductCode()) ||
                !existingInventory.getCompany().getNit().equalsIgnoreCase(requestDto.getCompanyNit())) {
            
            if (inventoryRepository.existsByProductCodeAndCompanyNit(requestDto.getProductCode(), requestDto.getCompanyNit())) {
                throw new EntityAlreadyExistsException("El registro de inventario para el producto '" +
                        requestDto.getProductCode() + "' en la empresa '" + requestDto.getCompanyNit() + "' ya existe.");
            }

            Product product = productRepository.findByCode(requestDto.getProductCode())
                    .orElseThrow(() -> new EntityNotFoundException("El producto con código " + requestDto.getProductCode() + " no existe."));

            Company company = companyRepository.findByNit(requestDto.getCompanyNit())
                    .orElseThrow(() -> new EntityNotFoundException("La empresa con NIT " + requestDto.getCompanyNit() + " no existe."));

            existingInventory.setProduct(product);
            existingInventory.setCompany(company);
        }

        existingInventory.setStock(requestDto.getStock());
        if (requestDto.getIsActive() != null) {
            existingInventory.setActive(requestDto.getIsActive());
        }

        Inventory updatedInventory = inventoryRepository.save(existingInventory);
        return InventoryMapper.toResponseDto(updatedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new EntityNotFoundException("El registro de inventario con ID " + id + " no fue encontrado.");
        }
        inventoryRepository.deleteById(id);
    }
}
