package com.lite.thinking.app.application.usecase;

import com.lite.thinking.app.application.dto.CategoryRequestDto;
import com.lite.thinking.app.application.dto.CategoryResponseDto;
import com.lite.thinking.app.domain.exception.EntityAlreadyExistsException;
import com.lite.thinking.app.domain.exception.EntityNotFoundException;
import com.lite.thinking.app.domain.model.Category;
import com.lite.thinking.app.domain.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_whenNameIsAvailable_savesCategory() {
        when(categoryRepository.existsByName("Tecnologia")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(1L);
            return category;
        });

        CategoryResponseDto response = categoryService.createCategory(request("Tecnologia", "Equipos", true));

        assertEquals(1L, response.getId());
        assertEquals("Tecnologia", response.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_whenNameExists_throwsEntityAlreadyExistsException() {
        when(categoryRepository.existsByName("Tecnologia")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> categoryService.createCategory(request("Tecnologia", "Equipos", true)));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryById_whenExists_returnsCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, "Tecnologia", true)));

        CategoryResponseDto response = categoryService.getCategoryById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Tecnologia", response.getName());
    }

    @Test
    void getAllCategories_returnsMappedCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(
                category(1L, "Tecnologia", true),
                category(2L, "Hogar", false)
        ));

        List<CategoryResponseDto> response = categoryService.getAllCategories();

        assertEquals(2, response.size());
        assertEquals("Hogar", response.get(1).getName());
        assertFalse(response.get(1).isActive());
    }

    @Test
    void updateCategory_whenNameChangesAndIsAvailable_updatesCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, "Tecnologia", true)));
        when(categoryRepository.existsByName("Hogar")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponseDto response = categoryService.updateCategory(1L, request("Hogar", "Casa", false));

        assertEquals("Hogar", response.getName());
        assertFalse(response.isActive());

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("Casa", captor.getValue().getDescription());
    }

    @Test
    void updateCategory_whenNameBelongsToAnotherCategory_throwsEntityAlreadyExistsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, "Tecnologia", true)));
        when(categoryRepository.existsByName("Hogar")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> categoryService.updateCategory(1L, request("Hogar", "Casa", true)));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_whenExists_deletesCategory() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_whenDoesNotExist_throwsEntityNotFoundException() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategory(1L));

        verify(categoryRepository, never()).deleteById(1L);
    }

    private CategoryRequestDto request(String name, String description, Boolean isActive) {
        return CategoryRequestDto.builder()
                .name(name)
                .description(description)
                .isActive(isActive)
                .build();
    }

    private Category category(Long id, String name, boolean isActive) {
        return Category.builder()
                .id(id)
                .name(name)
                .description("Descripcion")
                .isActive(isActive)
                .build();
    }
}
