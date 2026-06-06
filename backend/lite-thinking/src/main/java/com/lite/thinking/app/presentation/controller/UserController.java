package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.UserRequestDto;
import com.lite.thinking.app.application.dto.UserResponseDto;
import com.lite.thinking.app.application.usecase.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints para la gestión de usuarios")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario", description = "Registra un usuario con identificación única y le asigna un rol.")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Rol especificado no existe")
    @ApiResponse(responseCode = "409", description = "Usuario con esa identificación ya existe")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto createdUser = userService.createUser(requestDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID", description = "Retorna los detalles de un usuario por su ID.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/identification/{identification}")
    @Operation(summary = "Obtener un usuario por identificación", description = "Retorna los detalles de un usuario por su número de identificación.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserResponseDto> getUserByIdentification(@PathVariable String identification) {
        UserResponseDto user = userService.getUserByIdentification(identification);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario por ID", description = "Actualiza los datos de un usuario existente.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Usuario o Rol no encontrado")
    @ApiResponse(responseCode = "409", description = "La identificación ya está asignada a otro usuario")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto updatedUser = userService.updateUser(id, requestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario por ID", description = "Elimina un usuario por su ID.")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "Obtener usuarios por ID de rol", description = "Retorna una lista de usuarios asociados a un rol específico.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<List<UserResponseDto>> getUsersByRoleId(@PathVariable Long roleId) {
        List<UserResponseDto> users = userService.getUsersByRoleId(roleId);
        return ResponseEntity.ok(users);
    }
}
