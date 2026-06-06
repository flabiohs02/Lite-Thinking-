package com.lite.thinking.app.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "El código de producto no puede estar vacío")
    @Size(max = 50, message = "El código no puede superar los 50 caracteres")
    private String code;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(max = 150, message = "El nombre del producto no puede superar los 150 caracteres")
    private String name;

    @Size(max = 1000, message = "Las características no pueden superar los 1000 caracteres")
    private String characteristics;

    private String avatar;

    @NotEmpty(message = "Debe especificar al menos un precio en alguna moneda")
    @Valid
    private List<ProductPriceDto> prices;

    @NotBlank(message = "El NIT de la empresa es obligatorio")
    @Size(max = 50, message = "El NIT de la empresa no puede superar los 50 caracteres")
    private String companyNit;

    private List<Long> categoryIds;

    private Boolean isActive;
}
