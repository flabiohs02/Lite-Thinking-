package com.lite.thinking.app.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequestDto {

    @NotBlank(message = "El NIT no puede estar vacío")
    @Size(max = 50, message = "El NIT no puede superar los 50 caracteres")
    private String nit;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String address;

    @Size(max = 50, message = "El teléfono no puede superar los 50 caracteres")
    private String phone;

    private Boolean isActive;
}
