package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.OrderRequestDto;
import com.lite.thinking.app.application.dto.OrderResponseDto;
import com.lite.thinking.app.application.dto.UserResponseDto;
import com.lite.thinking.app.application.usecase.OrderService;
import com.lite.thinking.app.application.usecase.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")

@Tag(name = "Order", description = "Endpoints para la gestión de órdenes de usuarios")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva orden", description = "Registra una orden para un usuario con su detalle de productos y cantidades.")
    @ApiResponse(responseCode = "201", description = "Orden creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Usuario o uno de los productos/monedas no existe")
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto requestDto,
            Authentication authentication) {
        validateUserAccess(requestDto.getUserId(), authentication);
        OrderResponseDto createdOrder = orderService.createOrder(requestDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una orden por ID", description = "Retorna los detalles de una orden por su ID.")
    @ApiResponse(responseCode = "200", description = "Orden encontrada")
    @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(summary = "Obtener todas las órdenes", description = "Retorna una lista de todas las órdenes registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida exitosamente")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener órdenes por ID de usuario", description = "Retorna una lista de órdenes asociadas al usuario.")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(
            @PathVariable Long userId,
            Authentication authentication) {
        validateUserAccess(userId, authentication);
        List<OrderResponseDto> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una orden por ID", description = "Elimina una orden por su ID.")
    @ApiResponse(responseCode = "204", description = "Orden eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private void validateUserAccess(Long userId, Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            throw new AccessDeniedException("No tiene permisos para acceder a estas órdenes.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (isAdmin) {
            return;
        }

        UserResponseDto user = userService.getUserById(userId);
        if (!user.getIdentification().equals(authentication.getName())) {
            throw new AccessDeniedException("Solo puede gestionar sus propias órdenes.");
        }
    }
}
