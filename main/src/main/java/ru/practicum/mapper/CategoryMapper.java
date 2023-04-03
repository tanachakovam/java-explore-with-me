package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.practicum.model.Category;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDto(Page<Category> categories);

    Category toCategory(CategoryDto categoryDto);


}
