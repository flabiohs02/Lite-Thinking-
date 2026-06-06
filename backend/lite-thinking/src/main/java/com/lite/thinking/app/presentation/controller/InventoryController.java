package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.InventoryRequestDto;
import com.lite.thinking.app.application.dto.InventoryResponseDto;
import com.lite.thinking.app.application.usecase.InventoryService;
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
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Endpoints para la gestión del inventario de productos por empresa")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @Operation(summary = "Crear un nuevo registro de inventario", description = "Registra la relación de stock para un producto en una empresa.")
    @ApiResponse(responseCode = "201", description = "Registro creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Producto o Empresa no encontrado")
    @ApiResponse(responseCode = "409", description = "El registro para este producto y empresa ya existe")
    public ResponseEntity<InventoryResponseDto> createInventory(@Valid @RequestBody InventoryRequestDto requestDto) {
        InventoryResponseDto createdInventory = inventoryService.createInventory(requestDto);
        return new ResponseEntity<>(createdInventory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro de inventario por ID", description = "Retorna los detalles de inventario por su ID.")
    @ApiResponse(responseCode = "200", description = "Registro encontrado")
    @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    public ResponseEntity<InventoryResponseDto> getInventoryById(@PathVariable Long id) {
        InventoryResponseDto inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/product/{productCode}/company/{companyNit}")
    @Operation(summary = "Obtener inventario por producto y empresa", description = "Retorna el registro de stock buscando por el código del producto y el NIT de la empresa.")
    @ApiResponse(responseCode = "200", description = "Registro encontrado")
    @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    public ResponseEntity<InventoryResponseDto> getInventoryByProductAndCompany(
            @PathVariable String productCode,
            @PathVariable String companyNit) {
        InventoryResponseDto inventory = inventoryService.getInventoryByProductAndCompany(productCode, companyNit);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los registros de inventario", description = "Retorna una lista de todos los registros de stock.")
    @ApiResponse(responseCode = "200", description = "Lista de inventarios obtenida exitosamente")
    public ResponseEntity<List<InventoryResponseDto>> getAllInventories() {
        List<InventoryResponseDto> inventories = inventoryService.getAllInventories();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/product/{productCode}")
    @Operation(summary = "Obtener inventarios por producto", description = "Retorna todos los registros de stock para un producto en las diferentes empresas.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<List<InventoryResponseDto>> getInventoriesByProduct(@PathVariable String productCode) {
        List<InventoryResponseDto> inventories = inventoryService.getInventoriesByProduct(productCode);
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/company/{companyNit}")
    @Operation(summary = "Obtener inventarios por empresa", description = "Retorna todos los registros de stock de los productos asociados a una empresa específica.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    public ResponseEntity<List<InventoryResponseDto>> getInventoriesByCompany(@PathVariable String companyNit) {
        List<InventoryResponseDto> inventories = inventoryService.getInventoriesByCompany(companyNit);
        return ResponseEntity.ok(inventories);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un registro de inventario por ID", description = "Actualiza el producto, la empresa o el stock de un registro existente.")
    @ApiResponse(responseCode = "200", description = "Registro actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Registro, Producto o Empresa no encontrado")
    @ApiResponse(responseCode = "409", description = "El nuevo producto y empresa ya se encuentran asociados")
    public ResponseEntity<InventoryResponseDto> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequestDto requestDto) {
        InventoryResponseDto updatedInventory = inventoryService.updateInventory(id, requestDto);
        return ResponseEntity.ok(updatedInventory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un registro de inventario por ID", description = "Elimina un registro de stock por su ID.")
    @ApiResponse(responseCode = "204", description = "Registro eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
