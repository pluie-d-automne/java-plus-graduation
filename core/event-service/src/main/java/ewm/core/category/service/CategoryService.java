package ewm.core.category.service;

import ewm.core.category.dto.CategoryShortDto;

import java.util.List;

public interface CategoryService {
    ewm.core.dto.CategoryDto addCategory(CategoryShortDto newCategoryDto);

    void deleteCategoryById(Long categoryId);

    ewm.core.dto.CategoryDto updateCategory(Long categoryId, CategoryShortDto newCategoryDto);

    List<ewm.core.dto.CategoryDto> getAllCategory(Integer from, Integer size);

    ewm.core.dto.CategoryDto getCategoryById(Long catId);
}
