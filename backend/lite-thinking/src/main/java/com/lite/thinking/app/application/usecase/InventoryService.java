package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.InventoryRequestDto;
import com.lite.thinking.app.application.dto.InventoryResponseDto;
import java.util.List;

public interface InventoryService {
    InventoryResponseDto createInventory(InventoryRequestDto requestDto);
    InventoryResponseDto getInventoryById(Long id);
    InventoryResponseDto getInventoryByProductAndCompany(String productCode, String companyNit);
    List<InventoryResponseDto> getAllInventories();
    List<InventoryResponseDto> getInventoriesByProduct(String productCode);
    List<InventoryResponseDto> getInventoriesByCompany(String companyNit);
    InventoryResponseDto updateInventory(Long id, InventoryRequestDto requestDto);
    void deleteInventory(Long id);
}
