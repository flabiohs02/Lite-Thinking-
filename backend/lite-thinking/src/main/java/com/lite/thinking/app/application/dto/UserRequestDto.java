package com.lite.thinking.app.application.dto;

import jakarta.validation.constraints.Email;
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
public class UserRequestDto {

    @NotBlank(message = "La identificación del usuario no puede estar vacía")
    @Size(max = 50, message = "La identificación no puede superar los 50 caracteres")
    private String identification;

    @NotBlank(message = "El nombre del usuario no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @Email(message = "Debe proporcionar un formato de correo electrónico válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    private String email;

    @Size(max = 50, message = "El teléfono no puede superar los 50 caracteres")
    private String phone;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    private String password;

    @NotNull(message = "El ID de rol es obligatorio")
    private Long roleId;

    private Boolean isActive;
}
