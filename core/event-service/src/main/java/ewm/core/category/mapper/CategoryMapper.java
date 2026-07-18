package ewm.core.category.mapper;

import ewm.core.dto.CategoryDto;
import ewm.core.category.dto.NewCategoryDto;
import ewm.core.category.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toEntity(NewCategoryDto newCategoryDto);
}