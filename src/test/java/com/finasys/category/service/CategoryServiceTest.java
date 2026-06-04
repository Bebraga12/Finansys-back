package com.finasys.category.service;

import com.finasys.category.dto.CategoryRequest;
import com.finasys.category.dto.CategoryResponse;
import com.finasys.category.model.Category;
import com.finasys.category.repository.CategoryRepository;
import com.finasys.common.enums.UserPlan;
import com.finasys.common.exception.ResourceNotFoundException;
import com.finasys.user.model.User;
import com.finasys.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private UserService userService;
    @InjectMocks private CategoryService categoryService;

    @Test
    void shouldListOnlyUserCategories() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId);
        Category cat = buildCategory(UUID.randomUUID(), "Alimentação", user);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(categoryRepository.findByUserId(userId)).thenReturn(List.of(cat));

        List<CategoryResponse> result = categoryService.listAll("user@example.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Alimentação");
    }

    @Test
    void shouldCreateCategory() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(categoryRepository.save(any())).thenAnswer(inv -> {
            Category c = inv.getArgument(0); c.setId(UUID.randomUUID()); return c;
        });

        CategoryResponse result = categoryService.create("user@example.com",
                new CategoryRequest("Alimentação", false, true));

        assertThat(result.name()).isEqualTo("Alimentação");
        assertThat(result.expenseEnabled()).isTrue();
        assertThat(result.incomeEnabled()).isFalse();
    }

    @Test
    void shouldThrow404WhenCategoryNotFound() {
        UUID userId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        User user = buildUser(userId);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(catId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById("user@example.com", catId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDeleteCategory() {
        UUID userId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        User user = buildUser(userId);
        Category cat = buildCategory(catId, "Alimentação", user);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(catId, userId)).thenReturn(Optional.of(cat));

        categoryService.delete("user@example.com", catId);

        verify(categoryRepository).delete(cat);
    }

    private User buildUser(UUID id) {
        User u = new User(); u.setId(id); u.setEmail("user@example.com");
        u.setName("User"); u.setPasswordHash("hash"); u.setPlan(UserPlan.BASIC);
        return u;
    }

    private Category buildCategory(UUID id, String name, User user) {
        Category c = new Category(); c.setId(id); c.setName(name);
        c.setIncomeEnabled(false); c.setExpenseEnabled(true); c.setUser(user);
        return c;
    }
}
