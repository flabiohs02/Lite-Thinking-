package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.OrderRequestDto;
import com.lite.thinking.app.application.dto.OrderItemRequestDto;
import com.lite.thinking.app.application.dto.OrderResponseDto;
import com.lite.thinking.app.application.mapper.OrderMapper;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.*;
import com.lite.thinking.app.domain.repository.UserRepository;
import com.lite.thinking.app.domain.repository.ProductRepository;
import com.lite.thinking.app.domain.repository.OrderRepository;
import com.lite.thinking.app.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
            ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "El usuario con ID " + requestDto.getUserId() + " no existe. No se puede crear la orden."));

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : requestDto.getItems()) {
            Product product = productRepository.findByCode(itemDto.getProductCode())
                    .orElseThrow(() -> new EntityNotFoundException("El producto con código " + itemDto.getProductCode()
                            + " no existe. No se puede agregar a la orden."));

            if (!product.isActive()) {
                throw new EntityNotFoundException("El producto con código " + product.getCode()
                        + " no está activo. No se puede agregar a la orden.");
            }

            List<Inventory> availableInventories = inventoryRepository.findByProductCode(product.getCode()).stream()
                    .filter(Inventory::isActive)
                    .filter(inventory -> inventory.getStock() != null && inventory.getStock() > 0)
                    .sorted(Comparator.comparing(Inventory::getId))
                    .collect(Collectors.toList());

            int totalStock = availableInventories.stream()
                    .mapToInt(Inventory::getStock)
                    .sum();

            if (totalStock <= 1) {
                throw new EntityNotFoundException(
                        "El producto con código " + product.getCode() + " no tiene stock disponible para ordenar.");
            }

            if (itemDto.getQuantity() > totalStock) {
                throw new EntityNotFoundException("El producto con código " + product.getCode() + " solo tiene "
                        + totalStock + " unidades disponibles.");
            }

            ProductPrice priceInfo = product.getPrices().stream()
                    .filter(p -> p.getCurrency().equalsIgnoreCase(itemDto.getCurrency()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("El producto con código " + product.getCode()
                            + " no tiene un precio definido en la moneda " + itemDto.getCurrency()));

            discountStock(availableInventories, itemDto.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .price(priceInfo.getAmount())
                    .currency(priceInfo.getCurrency())
                    .isActive(true)
                    .build();

            items.add(orderItem);

            BigDecimal itemTotal = priceInfo.getAmount().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            total = total.add(itemTotal);
        }

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(requestDto.getStatus() == null || requestDto.getStatus().isBlank() ? "PENDING"
                        : requestDto.getStatus())
                .total(total)
                .items(items)
                .isActive(requestDto.getIsActive() == null || requestDto.getIsActive())
                .build();

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toResponseDto(savedOrder);
    }

    private void discountStock(List<Inventory> inventories, int quantity) {
        int remaining = quantity;

        for (Inventory inventory : inventories) {
            if (remaining == 0) {
                break;
            }

            int stock = inventory.getStock();
            int consumed = Math.min(stock, remaining);
            inventory.setStock(stock - consumed);
            inventoryRepository.save(inventory);
            remaining -= consumed;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La orden con ID " + id + " no fue encontrada."));
        return OrderMapper.toResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("El usuario con ID " + userId + " no existe.");
        }
        return orderRepository.findByUserId(userId).stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("La orden con ID " + id + " no fue encontrada.");
        }
        orderRepository.deleteById(id);
    }
}
