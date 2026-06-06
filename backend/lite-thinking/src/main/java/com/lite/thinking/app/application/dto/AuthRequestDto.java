package com.lite.thinking.app.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequestDto {

    @NotBlank(message = "La identificación es obligatoria")
    private String identification;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
