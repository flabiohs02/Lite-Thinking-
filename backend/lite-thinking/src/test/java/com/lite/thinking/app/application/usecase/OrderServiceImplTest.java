package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.OrderItemRequestDto;
import com.lite.thinking.app.application.dto.OrderRequestDto;
import com.lite.thinking.app.application.dto.OrderResponseDto;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Company;
import com.lite.thinking.app.domain.model.Inventory;
import com.lite.thinking.app.domain.model.Order;
import com.lite.thinking.app.domain.model.Product;
import com.lite.thinking.app.domain.model.ProductPrice;
import com.lite.thinking.app.domain.model.Role;
import com.lite.thinking.app.domain.model.User;
import com.lite.thinking.app.domain.repository.InventoryRepository;
import com.lite.thinking.app.domain.repository.OrderRepository;
import com.lite.thinking.app.domain.repository.ProductRepository;
import com.lite.thinking.app.domain.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_whenStockIsAvailable_createsOrderAndDiscountsInventory() {
        User user = clientUser();
        Product product = activeProduct();
        Inventory inventory = inventory(1L, product, 5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findByCode("P-001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductCode("P-001")).thenReturn(List.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(99L);
            return order;
        });

        OrderResponseDto response = orderService.createOrder(orderRequest(2, "APPROVED"));

        assertEquals(99L, response.getId());
        assertEquals(new BigDecimal("20.00"), response.getTotal());
        assertEquals(3, inventory.getStock());
        verify(inventoryRepository).save(inventory);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertEquals("APPROVED", savedOrder.getStatus());
        assertEquals(new BigDecimal("20.00"), savedOrder.getTotal());
        assertEquals(1, savedOrder.getItems().size());
        assertEquals(2, savedOrder.getItems().getFirst().getQuantity());
    }

    @Test
    void createOrder_whenProductIsInactive_throwsEntityNotFoundException() {
        Product product = activeProduct();
        product.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(clientUser()));
        when(productRepository.findByCode("P-001")).thenReturn(Optional.of(product));

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(orderRequest(1, "PENDING")));

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_whenStockIsNotEnough_throwsEntityNotFoundException() {
        Product product = activeProduct();
        Inventory inventory = inventory(1L, product, 1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(clientUser()));
        when(productRepository.findByCode("P-001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductCode("P-001")).thenReturn(List.of(inventory));

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(orderRequest(2, "PENDING")));

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_whenStatusIsBlank_usesPendingStatus() {
        Product product = activeProduct();
        Inventory inventory = inventory(1L, product, 4);

        when(userRepository.findById(1L)).thenReturn(Optional.of(clientUser()));
        when(productRepository.findByCode("P-001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductCode("P-001")).thenReturn(List.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(orderRequest(1, ""));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("PENDING", orderCaptor.getValue().getStatus());
    }

    @Test
    void createOrder_whenStockComesFromMultipleInventories_discountsInOrder() {
        Product product = activeProduct();
        Inventory firstInventory = inventory(1L, product, 1);
        Inventory secondInventory = inventory(2L, product, 4);

        when(userRepository.findById(1L)).thenReturn(Optional.of(clientUser()));
        when(productRepository.findByCode("P-001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductCode("P-001")).thenReturn(List.of(secondInventory, firstInventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(orderRequest(3, "PENDING"));

        assertEquals(0, firstInventory.getStock());
        assertEquals(2, secondInventory.getStock());
        verify(inventoryRepository).save(firstInventory);
        verify(inventoryRepository).save(secondInventory);
    }

    private OrderRequestDto orderRequest(int quantity, String status) {
        return OrderRequestDto.builder()
                .userId(1L)
                .status(status)
                .isActive(true)
                .items(List.of(OrderItemRequestDto.builder()
                        .productCode("P-001")
                        .quantity(quantity)
                        .currency("COP")
                        .build()))
                .build();
    }

    private User clientUser() {
        return User.builder()
                .id(1L)
                .identification("123")
                .name("Cliente")
                .role(Role.builder().id(2L).name("CLIENT").isActive(true).build())
                .isActive(true)
                .build();
    }

    private Product activeProduct() {
        return Product.builder()
                .code("P-001")
                .name("Producto")
                .company(Company.builder().nit("900").name("Empresa").isActive(true).build())
                .prices(List.of(ProductPrice.builder()
                        .currency("COP")
                        .amount(new BigDecimal("10.00"))
                        .build()))
                .isActive(true)
                .build();
    }

    private Inventory inventory(Long id, Product product, int stock) {
        return Inventory.builder()
                .id(id)
                .product(product)
                .company(product.getCompany())
                .stock(stock)
                .isActive(true)
                .build();
    }
}
