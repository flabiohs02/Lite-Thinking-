package com.lite.thinking.app.domain.repository;

import com.lite.thinking.app.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(Long id);
    List<Category> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByName(String name);
    List<Category> findAllByIds(List<Long> ids);
}
