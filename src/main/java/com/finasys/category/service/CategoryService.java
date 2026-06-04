package com.finasys.category.service;

import com.finasys.category.dto.CategoryRequest;
import com.finasys.category.dto.CategoryResponse;
import com.finasys.category.model.Category;
import com.finasys.category.repository.CategoryRepository;
import com.finasys.common.exception.ResourceNotFoundException;
import com.finasys.user.model.User;
import com.finasys.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public List<CategoryResponse> listAll(String email) {
        User user = userService.findUser(email);
        return categoryRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse).toList();
    }

    public CategoryResponse create(String email, CategoryRequest request) {
        User user = userService.findUser(email);
        Category category = new Category();
        category.setName(request.name());
        category.setIncomeEnabled(Boolean.TRUE.equals(request.incomeEnabled()));
        category.setExpenseEnabled(Boolean.TRUE.equals(request.expenseEnabled()));
        category.setUser(user);
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse getById(String email, UUID id) {
        User user = userService.findUser(email);
        return toResponse(findCategory(id, user.getId()));
    }

    public CategoryResponse update(String email, UUID id, CategoryRequest request) {
        User user = userService.findUser(email);
        Category category = findCategory(id, user.getId());
        category.setName(request.name());
        category.setIncomeEnabled(Boolean.TRUE.equals(request.incomeEnabled()));
        category.setExpenseEnabled(Boolean.TRUE.equals(request.expenseEnabled()));
        return toResponse(categoryRepository.save(category));
    }

    public void delete(String email, UUID id) {
        User user = userService.findUser(email);
        Category category = findCategory(id, user.getId());
        categoryRepository.delete(category);
    }

    private Category findCategory(UUID id, UUID userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(),
                category.isIncomeEnabled(), category.isExpenseEnabled());
    }
}
