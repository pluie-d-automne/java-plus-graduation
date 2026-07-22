package ewm.core.category.controller;

import ewm.core.category.dto.CategoryShortDto;
import ewm.core.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService adminCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ewm.core.dto.CategoryDto addCategories(@Valid @RequestBody CategoryShortDto newCategoryDto) {
        log.info("POST /admin/categories: {}", newCategoryDto);
        return adminCategoryService.addCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategories(@PathVariable("catId") Long categoryId) {
        log.info("DELETE /admin/categories/{}", categoryId);
        adminCategoryService.deleteCategoryById(categoryId);
    }

    @PatchMapping("/{catId}")
    public ewm.core.dto.CategoryDto updateCategory(@PathVariable("catId") Long categoryId,
                                                   @Valid @RequestBody CategoryShortDto newCategoryDto) {
        log.info("PATCH /admin/categories/{}: {}", categoryId, newCategoryDto);
        return adminCategoryService.updateCategory(categoryId, newCategoryDto);
    }
}
