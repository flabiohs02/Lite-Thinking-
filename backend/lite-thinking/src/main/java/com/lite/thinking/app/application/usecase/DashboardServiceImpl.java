package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.DashboardSummaryResponseDto;
import com.lite.thinking.app.domain.repository.CategoryRepository;
import com.lite.thinking.app.domain.repository.CompanyRepository;
import com.lite.thinking.app.domain.repository.InventoryRepository;
import com.lite.thinking.app.domain.repository.OrderRepository;
import com.lite.thinking.app.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;

    public DashboardServiceImpl(CompanyRepository companyRepository, ProductRepository productRepository,
            CategoryRepository categoryRepository,
            InventoryRepository inventoryRepository, OrderRepository orderRepository) {
        this.companyRepository = companyRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponseDto getSummary() {
        return DashboardSummaryResponseDto.builder()
                .totalCompanies(companyRepository.count())
                .totalProducts(productRepository.count())
                .totalCategories(categoryRepository.count())
                .totalInventories(inventoryRepository.count())
                .totalOrders(orderRepository.count())
                .build();
    }
}
