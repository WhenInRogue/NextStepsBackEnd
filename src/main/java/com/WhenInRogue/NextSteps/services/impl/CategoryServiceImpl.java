package com.WhenInRogue.NextSteps.services.impl;

import com.WhenInRogue.NextSteps.dtos.CategoryDTO;
import com.WhenInRogue.NextSteps.dtos.Response;
import com.WhenInRogue.NextSteps.enums.CategoryType;
import com.WhenInRogue.NextSteps.enums.UserRole;
import com.WhenInRogue.NextSteps.exceptions.ForbiddenException;
import com.WhenInRogue.NextSteps.exceptions.NameValueRequiredException;
import com.WhenInRogue.NextSteps.exceptions.NotFoundException;
import com.WhenInRogue.NextSteps.models.Category;
import com.WhenInRogue.NextSteps.models.User;
import com.WhenInRogue.NextSteps.repositories.CategoryRepository;
import com.WhenInRogue.NextSteps.services.CategoryService;
import com.WhenInRogue.NextSteps.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response createCategory(CategoryDTO categoryDTO) {
        requireAdmin();

        if (categoryDTO.getCategoryName() == null || categoryDTO.getCategoryName().isBlank()) {
            throw new NameValueRequiredException("Category name is required");
        }
        if (categoryDTO.getCategoryType() == null) {
            throw new NameValueRequiredException("Category type is required");
        }

        categoryRepository.findByCategoryName(categoryDTO.getCategoryName()).ifPresent(existing -> {
            throw new IllegalArgumentException("A category with that name already exists");
        });

        Category category = Category.builder()
                .categoryName(categoryDTO.getCategoryName())
                .description(categoryDTO.getDescription())
                .categoryType(categoryDTO.getCategoryType())
                .build();

        categoryRepository.save(category);

        return Response.builder()
                .status(200)
                .message("Category Created Successfully")
                .category(modelMapper.map(category, CategoryDTO.class))
                .build();
    }

    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepository.findAllByOrderByCategoryTypeAscCategoryNameAsc();

        List<CategoryDTO> categoryDTOs = modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .categories(categoryDTOs)
                .build();
    }

    @Override
    public Response getCategoriesByType(CategoryType categoryType) {
        List<Category> categories = categoryRepository.findByCategoryTypeOrderByCategoryNameAsc(categoryType);

        List<CategoryDTO> categoryDTOs = modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .categories(categoryDTOs)
                .build();
    }

    @Override
    public Response getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category Not Found"));

        return Response.builder()
                .status(200)
                .message("success")
                .category(modelMapper.map(category, CategoryDTO.class))
                .build();
    }

    @Override
    @Transactional
    public Response updateCategory(Long id, CategoryDTO categoryDTO) {
        requireAdmin();

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category Not Found"));

        if (categoryDTO.getCategoryName() != null) {
            categoryRepository.findByCategoryName(categoryDTO.getCategoryName())
                    .filter(other -> !other.getCategoryId().equals(id))
                    .ifPresent(other -> {
                        throw new IllegalArgumentException("A category with that name already exists");
                    });
            existingCategory.setCategoryName(categoryDTO.getCategoryName());
        }
        if (categoryDTO.getDescription() != null) {
            existingCategory.setDescription(categoryDTO.getDescription());
        }
        if (categoryDTO.getCategoryType() != null) {
            existingCategory.setCategoryType(categoryDTO.getCategoryType());
        }

        categoryRepository.save(existingCategory);

        return Response.builder()
                .status(200)
                .message("Category Updated Successfully")
                .category(modelMapper.map(existingCategory, CategoryDTO.class))
                .build();
    }

    @Override
    @Transactional
    public Response deleteCategory(Long id) {
        requireAdmin();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category Not Found"));

        categoryRepository.delete(category);

        return Response.builder()
                .status(200)
                .message("Category Deleted Successfully")
                .build();
    }

    private void requireAdmin() {
        User currentUser = userService.getCurrentLoggedInUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only an admin can manage categories");
        }
    }
}
