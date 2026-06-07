package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.CategoryRequestDto;
import com.lite.thinking.app.application.dto.CategoryResponseDto;
import com.lite.thinking.app.application.mapper.CategoryMapper;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Category;
import com.lite.thinking.app.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        if (categoryRepository.existsByName(requestDto.getName())) {
            throw new EntityAlreadyExistsException("La categoría con nombre '" + requestDto.getName() + "' ya existe.");
        }
        Category category = CategoryMapper.toDomain(requestDto);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toResponseDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La categoría con ID " + id + " no fue encontrada."));
        return CategoryMapper.toResponseDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La categoría con ID " + id + " no fue encontrada."));

        if (!existingCategory.getName().equalsIgnoreCase(requestDto.getName()) &&
                categoryRepository.existsByName(requestDto.getName())) {
            throw new EntityAlreadyExistsException("La categoría con nombre '" + requestDto.getName() + "' ya existe.");
        }

        existingCategory.setName(requestDto.getName());
        existingCategory.setDescription(requestDto.getDescription());
        if (requestDto.getIsActive() != null) {
            existingCategory.setActive(requestDto.getIsActive());
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return CategoryMapper.toResponseDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("La categoría con ID " + id + " no fue encontrada.");
        }
        categoryRepository.deleteById(id);
    }
}
