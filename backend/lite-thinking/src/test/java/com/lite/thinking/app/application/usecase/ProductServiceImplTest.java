package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.ProductPriceDto;
import com.lite.thinking.app.application.dto.ProductRequestDto;
import com.lite.thinking.app.application.dto.ProductResponseDto;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Category;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.domain.model.Product;
import com.lite.thinking.app.domain.model.ProductPrice;
import com.lite.thinking.app.domain.repository.CategoryRepository;
import com.lite.thinking.app.domain.repository.CompanyRepository;
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
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_whenDependenciesExist_savesProduct() {
        Company company = company();
        List<Category> categories = List.of(category(1L, "Tecnologia"));
        when(productRepository.existsByCode("P-001")).thenReturn(false);
        when(companyRepository.findByNit("900")).thenReturn(Optional.of(company));
        when(categoryRepository.findAllByIds(List.of(1L))).thenReturn(categories);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto response = productService.createProduct(request("P-001", "Laptop", "900", List.of(1L), true));

        assertEquals("P-001", response.getCode());
        assertEquals("Empresa", response.getCompany().getName());
        assertEquals(1, response.getCategories().size());
        assertEquals(new BigDecimal("100.00"), response.getPrices().getFirst().getAmount());
    }

    @Test
    void createProduct_whenCodeExists_throwsEntityAlreadyExistsException() {
        when(productRepository.existsByCode("P-001")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> productService.createProduct(request("P-001", "Laptop", "900", List.of(1L), true)));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_whenCompanyDoesNotExist_throwsEntityNotFoundException() {
        when(productRepository.existsByCode("P-001")).thenReturn(false);
        when(companyRepository.findByNit("900")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> productService.createProduct(request("P-001", "Laptop", "900", List.of(1L), true)));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_whenSomeCategoryDoesNotExist_throwsEntityNotFoundException() {
        when(productRepository.existsByCode("P-001")).thenReturn(false);
        when(companyRepository.findByNit("900")).thenReturn(Optional.of(company()));
        when(categoryRepository.findAllByIds(List.of(1L, 2L))).thenReturn(List.of(category(1L, "Tecnologia")));

        assertThrows(EntityNotFoundException.class,
                () -> productService.createProduct(request("P-001", "Laptop", "900", List.of(1L, 2L), true)));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductByCode_whenExists_returnsProduct() {
        when(productRepository.findByCode("P-001")).thenReturn(Optional.of(product("P-001", true)));

        ProductResponseDto response = productService.getProductByCode("P-001");

        assertEquals("Laptop", response.getName());
    }

    @Test
    void getAllProducts_returnsMappedProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product("P-001", true), product("P-002", false)));

        List<ProductResponseDto> response = productService.getAllProducts();

        assertEquals(2, response.size());
        assertFalse(response.get(1).isActive());
    }

    @Test
    void updateProduct_whenDependenciesExist_updatesProduct() {
        Product existingProduct = product("P-001", true);
        when(productRepository.findByCode("P-001")).thenReturn(Optional.of(existingProduct));
        when(companyRepository.findByNit("900")).thenReturn(Optional.of(company()));
        when(categoryRepository.findAllByIds(List.of(2L))).thenReturn(List.of(category(2L, "Hogar")));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto response = productService.updateProduct("P-001",
                request("P-999", "Monitor", "900", List.of(2L), false));

        assertEquals("P-001", response.getCode());
        assertEquals("Monitor", response.getName());
        assertFalse(response.isActive());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals("avatar-base64", captor.getValue().getAvatar());
        assertEquals("Hogar", captor.getValue().getCategories().getFirst().getName());
    }

    @Test
    void deleteProduct_whenExists_deletesProduct() {
        when(productRepository.existsByCode("P-001")).thenReturn(true);

        productService.deleteProduct("P-001");

        verify(productRepository).deleteByCode("P-001");
    }

    @Test
    void getProductsByCompanyNit_whenCompanyExists_returnsProducts() {
        when(companyRepository.existsByNit("900")).thenReturn(true);
        when(productRepository.findByCompanyNit("900")).thenReturn(List.of(product("P-001", true)));

        List<ProductResponseDto> response = productService.getProductsByCompanyNit("900");

        assertEquals(1, response.size());
        assertEquals("P-001", response.getFirst().getCode());
    }

    @Test
    void getProductsByCompanyNit_whenCompanyDoesNotExist_throwsEntityNotFoundException() {
        when(companyRepository.existsByNit("900")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> productService.getProductsByCompanyNit("900"));

        verify(productRepository, never()).findByCompanyNit("900");
    }

    private ProductRequestDto request(String code, String name, String companyNit, List<Long> categoryIds, Boolean isActive) {
        return ProductRequestDto.builder()
                .code(code)
                .name(name)
                .characteristics("Caracteristicas")
                .avatar("avatar-base64")
                .prices(List.of(ProductPriceDto.builder()
                        .currency("COP")
                        .amount(new BigDecimal("100.00"))
                        .build()))
                .companyNit(companyNit)
                .categoryIds(categoryIds)
                .isActive(isActive)
                .build();
    }

    private Product product(String code, boolean isActive) {
        return Product.builder()
                .code(code)
                .name("Laptop")
                .characteristics("Caracteristicas")
                .avatar("avatar")
                .company(company())
                .categories(List.of(category(1L, "Tecnologia")))
                .prices(List.of(ProductPrice.builder()
                        .currency("COP")
                        .amount(new BigDecimal("100.00"))
                        .build()))
                .isActive(isActive)
                .build();
    }

    private Company company() {
        return Company.builder()
                .nit("900")
                .name("Empresa")
                .isActive(true)
                .build();
    }

    private Category category(Long id, String name) {
        return Category.builder()
                .id(id)
                .name(name)
                .description("Descripcion")
                .isActive(true)
                .build();
    }
}
