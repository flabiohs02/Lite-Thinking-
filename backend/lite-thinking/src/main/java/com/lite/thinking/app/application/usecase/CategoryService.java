package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.CategoryRequestDto;
import com.lite.thinking.app.application.dto.CategoryResponseDto;
import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto requestDto);
    CategoryResponseDto getCategoryById(Long id);
    List<CategoryResponseDto> getAllCategories();
    CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto);
    void deleteCategory(Long id);
}
