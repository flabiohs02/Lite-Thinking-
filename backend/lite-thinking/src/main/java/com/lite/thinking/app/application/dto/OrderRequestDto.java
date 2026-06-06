package com.lite.thinking.app.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    private String status;

    @NotEmpty(message = "La orden debe contener al menos un producto")
    @Valid
    private List<OrderItemRequestDto> items;

    private Boolean isActive;
}
