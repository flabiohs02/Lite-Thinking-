package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.OrderRequestDto;
import com.lite.thinking.app.application.dto.OrderResponseDto;
import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto requestDto);
    OrderResponseDto getOrderById(Long id);
    List<OrderResponseDto> getAllOrders();
    List<OrderResponseDto> getOrdersByUserId(Long userId);
    void deleteOrder(Long id);
}
