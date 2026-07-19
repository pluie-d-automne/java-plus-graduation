package ewm.core.category.mapper;

import ewm.core.category.dto.CategoryShortDto;
import ewm.core.category.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    ewm.core.dto.CategoryDto toDto(Category category);

    Category toEntity(CategoryShortDto newCategoryDto);
}