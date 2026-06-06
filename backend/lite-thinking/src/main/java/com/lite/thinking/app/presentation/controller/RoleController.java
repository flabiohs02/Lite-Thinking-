package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.RoleRequestDto;
import com.lite.thinking.app.application.dto.RoleResponseDto;
import com.lite.thinking.app.application.usecase.RoleService;
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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Endpoints para la gestión de roles de usuario")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Crear un nuevo rol", description = "Registra un rol con nombre único.")
    @ApiResponse(responseCode = "201", description = "Rol creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "El rol con ese nombre ya existe")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto requestDto) {
        RoleResponseDto createdRole = roleService.createRole(requestDto);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un rol por ID", description = "Retorna los detalles de un rol por su ID.")
    @ApiResponse(responseCode = "200", description = "Rol encontrado")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
        RoleResponseDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Obtener un rol por nombre", description = "Retorna los detalles de un rol por su nombre.")
    @ApiResponse(responseCode = "200", description = "Rol encontrado")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<RoleResponseDto> getRoleByName(@PathVariable String name) {
        RoleResponseDto role = roleService.getRoleByName(name);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los roles", description = "Retorna una lista de todos los roles registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        List<RoleResponseDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un rol por ID", description = "Actualiza los datos de un rol existente.")
    @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ApiResponse(responseCode = "409", description = "El nombre del rol ya está en uso")
    public ResponseEntity<RoleResponseDto> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDto requestDto) {
        RoleResponseDto updatedRole = roleService.updateRole(id, requestDto);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un rol por ID", description = "Elimina un rol por su ID.")
    @ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
