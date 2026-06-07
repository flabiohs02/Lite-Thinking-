package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.CompanyRequestDto;
import com.lite.thinking.app.application.dto.CompanyResponseDto;
import com.lite.thinking.app.application.usecase.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Company", description = "Endpoints para la gestión de empresas")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva empresa", description = "Registra una empresa con NIT único.")
    @ApiResponse(responseCode = "201", description = "Empresa creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "La empresa con ese NIT ya existe")
    public ResponseEntity<CompanyResponseDto> createCompany(@Valid @RequestBody CompanyRequestDto requestDto) {
        CompanyResponseDto createdCompany = companyService.createCompany(requestDto);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @GetMapping("/{nit}")
    @Operation(summary = "Obtener una empresa por NIT", description = "Retorna los detalles de la empresa correspondiente al NIT provisto.")
    @ApiResponse(responseCode = "200", description = "Empresa encontrada")
    @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    public ResponseEntity<CompanyResponseDto> getCompanyByNit(@PathVariable String nit) {
        CompanyResponseDto company = companyService.getCompanyByNit(nit);
        return ResponseEntity.ok(company);
    }

    @GetMapping
    @Operation(summary = "Obtener todas las empresas", description = "Retorna una lista con todas las empresas registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de empresas obtenida exitosamente")
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {
        List<CompanyResponseDto> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    @PutMapping("/{nit}")
    @Operation(summary = "Actualizar una empresa por NIT", description = "Actualiza los datos de una empresa existente.")
    @ApiResponse(responseCode = "200", description = "Empresa actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    public ResponseEntity<CompanyResponseDto> updateCompany(
            @PathVariable String nit,
            @Valid @RequestBody CompanyRequestDto requestDto) {
        CompanyResponseDto updatedCompany = companyService.updateCompany(nit, requestDto);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/{nit}")
    @Operation(summary = "Eliminar una empresa por NIT", description = "Elimina la empresa con el NIT especificado.")
    @ApiResponse(responseCode = "204", description = "Empresa eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCompany(@PathVariable String nit) {
        companyService.deleteCompany(nit);
        return ResponseEntity.noContent().build();
    }
}
