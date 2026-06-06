package com.lite.thinking.app.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPriceDto {

    @NotBlank(message = "La moneda no puede estar vacía")
    @Size(max = 10, message = "El código de la moneda no puede superar los 10 caracteres")
    private String currency;

    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto debe ser mayor o igual a 0")
    private BigDecimal amount;
}
