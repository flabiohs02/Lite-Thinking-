package com.lite.thinking.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponseDto {
    private long totalCompanies;
    private long totalProducts;
    private long totalCategories;
    private long totalInventories;
    private long totalOrders;
}
