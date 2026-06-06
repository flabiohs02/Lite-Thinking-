package com.lite.thinking.app.application.mapper;

import com.lite.thinking.app.application.dto.OrderItemResponseDto;
import com.lite.thinking.app.application.dto.OrderResponseDto;
import com.lite.thinking.app.domain.model.Order;
import com.lite.thinking.app.domain.model.OrderItem;
import com.lite.thinking.app.infrastructure.persistence.entity.OrderEntity;
import com.lite.thinking.app.infrastructure.persistence.entity.OrderItemEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderItem toDomain(OrderItemEntity entity) {
        if (entity == null) {
            return null;
        }
        return OrderItem.builder()
                .id(entity.getId())
                .product(ProductMapper.toDomain(entity.getProduct()))
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .currency(entity.getCurrency())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static OrderItemEntity toEntity(OrderItem domain, OrderEntity orderEntity) {
        if (domain == null) {
            return null;
        }
        OrderItemEntity entity = OrderItemEntity.builder()
                .id(domain.getId())
                .order(orderEntity)
                .product(ProductMapper.toEntity(domain.getProduct()))
                .quantity(domain.getQuantity())
                .price(domain.getPrice())
                .currency(domain.getCurrency())
                .build();
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        List<OrderItem> items = entity.getItems() == null ? Collections.emptyList() :
                entity.getItems().stream().map(OrderMapper::toDomain).collect(Collectors.toList());

        return Order.builder()
                .id(entity.getId())
                .user(UserMapper.toDomain(entity.getUser()))
                .orderDate(entity.getOrderDate())
                .status(entity.getStatus())
                .total(entity.getTotal())
                .items(items)
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static OrderEntity toEntity(Order domain) {
        if (domain == null) {
            return null;
        }
        OrderEntity orderEntity = OrderEntity.builder()
                .id(domain.getId())
                .user(UserMapper.toEntity(domain.getUser()))
                .orderDate(domain.getOrderDate())
                .status(domain.getStatus())
                .total(domain.getTotal())
                .build();
        orderEntity.setActive(domain.isActive());
        orderEntity.setCreatedAt(domain.getCreatedAt());
        orderEntity.setUpdatedAt(domain.getUpdatedAt());

        List<OrderItemEntity> itemEntities = domain.getItems() == null ? new ArrayList<>() :
                domain.getItems().stream()
                        .map(item -> OrderMapper.toEntity(item, orderEntity))
                        .collect(Collectors.toList());

        orderEntity.setItems(itemEntities);
        return orderEntity;
    }

    public static OrderItemResponseDto toResponseDto(OrderItem domain) {
        if (domain == null) {
            return null;
        }
        return OrderItemResponseDto.builder()
                .id(domain.getId())
                .productCode(domain.getProduct().getCode())
                .productName(domain.getProduct().getName())
                .quantity(domain.getQuantity())
                .price(domain.getPrice())
                .currency(domain.getCurrency())
                .isActive(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public static OrderResponseDto toResponseDto(Order domain) {
        if (domain == null) {
            return null;
        }
        List<OrderItemResponseDto> itemDtos = domain.getItems() == null ? Collections.emptyList() :
                domain.getItems().stream().map(OrderMapper::toResponseDto).collect(Collectors.toList());

        return OrderResponseDto.builder()
                .id(domain.getId())
                .user(UserMapper.toResponseDto(domain.getUser()))
                .orderDate(domain.getOrderDate())
                .status(domain.getStatus())
                .total(domain.getTotal())
                .items(itemDtos)
                .isActive(domain.isActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
