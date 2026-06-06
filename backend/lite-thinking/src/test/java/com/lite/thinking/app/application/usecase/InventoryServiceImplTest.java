package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.InventoryRequestDto;
import com.lite.thinking.app.application.dto.InventoryResponseDto;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.domain.model.Inventory;
import com.lite.thinking.app.domain.model.Product;
import com.lite.thinking.app.domain.model.ProductPrice;
import com.lite.thinking.app.domain.repository.CompanyRepository;
import com.lite.thinking.app.domain.repository.InventoryRepository;
import com.lite.thinking.app.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Test
    void createInventory_whenProductAndCompanyExist_savesInventory() {
        Product product = product();
        Company company = company();
        when(inventoryRepository.existsByProductCodeAndCompanyNit("P-001", "900")).thenReturn(false);
        when(productRepository.findByCode("P-001")).thenReturn(Optional.of(product));
        when(companyRepository.findByNit("900")).thenReturn(Optional.of(company));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> {
            Inventory inventory = invocation.getArgument(0);
            inventory.setId(1L);
            return inventory;
        });

        InventoryResponseDto response = inventoryService.createInventory(request("P-001", "900", 10, true));

        assertEquals(1L, response.getId());
        assertEquals(10, response.getStock());
        assertEquals("P-001", response.getProduct().getCode());
    }

    @Test
    void createInventory_whenInventoryExists_throwsEntityAlreadyExistsException() {
        when(inventoryRepository.existsByProductCodeAndCompanyNit("P-001", "900")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> inventoryService.createInventory(request("P-001", "900", 10, true)));

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void createInventory_whenProductDoesNotExist_throwsEntityNotFoundException() {
        when(inventoryRepository.existsByProductCodeAndCompanyNit("P-001", "900")).thenReturn(false);
        when(productRepository.findByCode("P-001")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> inventoryService.createInventory(request("P-001", "900", 10, true)));

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void getInventoryById_whenExists_returnsInventory() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory(1L, product(), company(), 10, true)));

        InventoryResponseDto response = inventoryService.getInventoryById(1L);

        assertEquals(10, response.getStock());
    }

    @Test
    void getInventoryByProductAndCompany_whenExists_returnsInventory() {
        when(inventoryRepository.findByProductCodeAndCompanyNit("P-001", "900"))
                .thenReturn(Optional.of(inventory(1L, product(), company(), 10, true)));

        InventoryResponseDto response = inventoryService.getInventoryByProductAndCompany("P-001", "900");

        assertEquals("900", response.getCompany().getNit());
    }

    @Test
    void getAllInventories_returnsMappedInventories() {
        when(inventoryRepository.findAll()).thenReturn(List.of(
                inventory(1L, product(), company(), 10, true),
                inventory(2L, product(), company(), 0, false)
        ));

        List<InventoryResponseDto> response = inventoryService.getAllInventories();

        assertEquals(2, response.size());
        assertFalse(response.get(1).isActive());
    }

    @Test
    void getInventoriesByProduct_whenProductExists_returnsInventories() {
        when(productRepository.existsByCode("P-001")).thenReturn(true);
        when(inventoryRepository.findByProductCode("P-001"))
                .thenReturn(List.of(inventory(1L, product(), company(), 10, true)));

        List<InventoryResponseDto> response = inventoryService.getInventoriesByProduct("P-001");

        assertEquals(1, response.size());
    }

    @Test
    void getInventoriesByCompany_whenCompanyExists_returnsInventories() {
        when(companyRepository.existsByNit("900")).thenReturn(true);
        when(inventoryRepository.findByCompanyNit("900"))
                .thenReturn(List.of(inventory(1L, product(), company(), 10, true)));

        List<InventoryResponseDto> response = inventoryService.getInventoriesByCompany("900");

        assertEquals(1, response.size());
    }

    @Test
    void updateInventory_whenProductAndCompanyDoNotChange_updatesStockAndActiveState() {
        Inventory existingInventory = inventory(1L, product(), company(), 10, true);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponseDto response = inventoryService.updateInventory(1L, request("P-001", "900", 5, false));

        assertEquals(5, response.getStock());
        assertFalse(response.isActive());
    }

    @Test
    void updateInventory_whenProductOrCompanyChanges_updatesAssociations() {
        Product newProduct = product("P-002");
        Company newCompany = company("901");
        Inventory existingInventory = inventory(1L, product(), company(), 10, true);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.existsByProductCodeAndCompanyNit("P-002", "901")).thenReturn(false);
        when(productRepository.findByCode("P-002")).thenReturn(Optional.of(newProduct));
        when(companyRepository.findByNit("901")).thenReturn(Optional.of(newCompany));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponseDto response = inventoryService.updateInventory(1L, request("P-002", "901", 7, true));

        assertEquals("P-002", response.getProduct().getCode());
        assertEquals("901", response.getCompany().getNit());

        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(captor.capture());
        assertEquals(7, captor.getValue().getStock());
    }

    @Test
    void updateInventory_whenNewProductAndCompanyAlreadyExists_throwsEntityAlreadyExistsException() {
        Inventory existingInventory = inventory(1L, product(), company(), 10, true);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.existsByProductCodeAndCompanyNit("P-002", "901")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> inventoryService.updateInventory(1L, request("P-002", "901", 7, true)));

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void deleteInventory_whenExists_deletesInventory() {
        when(inventoryRepository.existsById(1L)).thenReturn(true);

        inventoryService.deleteInventory(1L);

        verify(inventoryRepository).deleteById(1L);
    }

    @Test
    void deleteInventory_whenDoesNotExist_throwsEntityNotFoundException() {
        when(inventoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> inventoryService.deleteInventory(1L));

        verify(inventoryRepository, never()).deleteById(1L);
    }

    private InventoryRequestDto request(String productCode, String companyNit, int stock, Boolean isActive) {
        return InventoryRequestDto.builder()
                .productCode(productCode)
                .companyNit(companyNit)
                .stock(stock)
                .isActive(isActive)
                .build();
    }

    private Inventory inventory(Long id, Product product, Company company, int stock, boolean isActive) {
        return Inventory.builder()
                .id(id)
                .product(product)
                .company(company)
                .stock(stock)
                .isActive(isActive)
                .build();
    }

    private Product product() {
        return product("P-001");
    }

    private Product product(String code) {
        return Product.builder()
                .code(code)
                .name("Producto")
                .company(company())
                .prices(List.of(ProductPrice.builder()
                        .currency("COP")
                        .amount(new BigDecimal("100.00"))
                        .build()))
                .isActive(true)
                .build();
    }

    private Company company() {
        return company("900");
    }

    private Company company(String nit) {
        return Company.builder()
                .nit(nit)
                .name("Empresa")
                .isActive(true)
                .build();
    }
}
