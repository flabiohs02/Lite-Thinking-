package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.ProductRequestDto;
import com.lite.thinking.app.application.dto.ProductResponseDto;
import com.lite.thinking.app.application.usecase.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Endpoints para la gestión de productos")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Registra un producto con código único y precios en varias monedas.")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o la empresa asociada no existe")
    @ApiResponse(responseCode = "409", description = "El producto con ese código ya existe")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto createdProduct = productService.createProduct(requestDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Obtener un producto por código", description = "Retorna los detalles de un producto por su código.")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<ProductResponseDto> getProductByCode(@PathVariable String code) {
        ProductResponseDto product = productService.getProductByCode(code);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Retorna una lista de todos los productos registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Actualizar un producto por código", description = "Actualiza los datos de un producto existente y sus precios.")
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o la empresa asociada no existe")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable String code,
            @Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto updatedProduct = productService.updateProduct(code, requestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Eliminar un producto por código", description = "Elimina un producto por su código.")
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteProduct(@PathVariable String code) {
        productService.deleteProduct(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/company/{companyNit}")
    @Operation(summary = "Obtener productos por NIT de empresa", description = "Retorna una lista de productos que pertenecen a la empresa con el NIT especificado.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "La empresa no existe")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCompanyNit(@PathVariable String companyNit) {
        List<ProductResponseDto> products = productService.getProductsByCompanyNit(companyNit);
        return ResponseEntity.ok(products);
    }
}
