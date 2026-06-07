package com.lite.thinking.app.presentation.controller;

import com.lite.thinking.app.application.dto.DashboardSummaryResponseDto;
import com.lite.thinking.app.application.usecase.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")

@Tag(name = "Dashboard", description = "Endpoints para el resumen del dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Obtener resumen del dashboard", description = "Retorna conteos de empresas, productos, categorías, inventarios y órdenes.")
    public ResponseEntity<DashboardSummaryResponseDto> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
