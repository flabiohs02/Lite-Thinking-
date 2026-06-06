package com.lite.thinking.app.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequestDto {

    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 50, message = "El código del producto no puede superar los 50 caracteres")
    private String productCode;

    @NotBlank(message = "El NIT de la empresa es obligatorio")
    @Size(max = 50, message = "El NIT de la empresa no puede superar los 50 caracteres")
    private String companyNit;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock mínimo debe ser 0")
    private Integer stock;

    private Boolean isActive;
}
