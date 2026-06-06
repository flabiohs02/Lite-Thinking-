package com.lite.thinking.app.infrastructure.persistence.adapter;

import com.lite.thinking.app.application.mapper.CategoryMapper;
import com.lite.thinking.app.domain.model.Category;
import com.lite.thinking.app.domain.repository.CategoryRepository;
import com.lite.thinking.app.infrastructure.persistence.entity.CategoryEntity;
import com.lite.thinking.app.infrastructure.persistence.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = CategoryMapper.toEntity(category);
        CategoryEntity savedEntity = categoryJpaRepository.save(entity);
        return CategoryMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id)
                .map(CategoryMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAll().stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        categoryJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return categoryJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryJpaRepository.existsByName(name);
    }

    @Override
    public List<Category> findAllByIds(List<Long> ids) {
        return categoryJpaRepository.findAllById(ids).stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());
    }
}
