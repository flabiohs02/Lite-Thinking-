package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.CategoryRequestDto;
import com.lite.thinking.app.application.dto.CategoryResponseDto;
import com.lite.thinking.app.application.usecase.CategoryService;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Endpoints para la gestión de categorías de productos")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Crear una nueva categoría", description = "Registra una categoría con nombre único.")
    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "La categoría con ese nombre ya existe")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        CategoryResponseDto createdCategory = categoryService.createCategory(requestDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una categoría por ID", description = "Retorna los detalles de una categoría por su ID.")
    @ApiResponse(responseCode = "200", description = "Categoría encontrada")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    @Operation(summary = "Obtener todas las categorías", description = "Retorna una lista de todas las categorías registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una categoría por ID", description = "Actualiza los datos de una categoría existente.")
    @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @ApiResponse(responseCode = "409", description = "El nombre de categoría ya está en uso")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto requestDto) {
        CategoryResponseDto updatedCategory = categoryService.updateCategory(id, requestDto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una categoría por ID", description = "Elimina una categoría por su ID.")
    @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
