package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.exception.ValidationException;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.service.CategoryService;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addNewCategory(CategoryDto categoryDto) {
        checkCategoryName(categoryDto.getName());
        Category category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ValidationException("The category is not empty.");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category categoryToUpdate = findCategoryById(catId);
        checkCategoryName(categoryDto.getName());
        categoryMapper.map(categoryDto, categoryToUpdate);

        categoryRepository.save(categoryToUpdate);
        return categoryMapper.toCategoryDto(categoryToUpdate);
    }

    @Transactional(readOnly = true)
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category " + id + " is not found"));
    }

    @Transactional(readOnly = true)
    public void checkCategoryName(String name) {
        if (categoryRepository.findCategoryByName(name) != null) {
            throw new ValidationException("Category name already exists.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Pageable pageable) {
        return categoryMapper.toCategoryDto(categoryRepository.findAll(pageable));

    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long catId) {
        return categoryMapper.toCategoryDto(findCategoryById(catId));
    }
}

