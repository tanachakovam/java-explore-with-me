package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDto(Page<Category> categories);

    Category toCategory(CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    void map(CategoryDto categoryDto, @MappingTarget Category category);
}
