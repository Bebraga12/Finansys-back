package com.finasys.transaction.service;

import com.finasys.category.model.Category;
import com.finasys.category.repository.CategoryRepository;
import com.finasys.common.enums.TransactionType;
import com.finasys.common.enums.UserPlan;
import com.finasys.common.exception.ResourceNotFoundException;
import com.finasys.transaction.dto.TransactionRequest;
import com.finasys.transaction.dto.TransactionResponse;
import com.finasys.transaction.model.Transaction;
import com.finasys.transaction.repository.TransactionRepository;
import com.finasys.user.model.User;
import com.finasys.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserService userService;
    @InjectMocks private TransactionService transactionService;

    @Test
    void shouldCreateIncomeWithoutCategory() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(transactionRepository.save(any())).thenAnswer(inv -> { Transaction t = inv.getArgument(0); t.setId(UUID.randomUUID()); return t; });

        TransactionResponse response = transactionService.create("user@example.com",
                new TransactionRequest(TransactionType.INCOME, new BigDecimal("100.00"), null, "Salário", LocalDate.now()));

        assertThat(response.type()).isEqualTo(TransactionType.INCOME);
        assertThat(response.amount()).isEqualByComparingTo("100.00");
    }

    @Test
    void shouldCreateExpenseWithCategory() {
        UUID userId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        User user = buildUser(userId);
        Category category = buildCategory(catId, "Alimentação", user);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(catId, userId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any())).thenAnswer(inv -> { Transaction t = inv.getArgument(0); t.setId(UUID.randomUUID()); return t; });

        TransactionResponse response = transactionService.create("user@example.com",
                new TransactionRequest(TransactionType.EXPENSE, new BigDecimal("50.00"), catId, "Almoço", LocalDate.now()));

        assertThat(response.type()).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    void shouldThrowWhenExpenseHasNoCategory() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId);
        when(userService.findUser("user@example.com")).thenReturn(user);

        assertThatThrownBy(() -> transactionService.create("user@example.com",
                new TransactionRequest(TransactionType.EXPENSE, new BigDecimal("50.00"), null, "Sem categoria", LocalDate.now())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Categoria");
    }

    @Test
    void shouldListOnlyUserTransactions() {
        UUID userId = UUID.randomUUID();
        User user = buildUser(userId);
        Transaction t = buildTransaction(UUID.randomUUID(), TransactionType.INCOME, new BigDecimal("100"), user);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(t));

        List<TransactionResponse> result = transactionService.list("user@example.com", null, null, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldThrow404WhenTransactionNotFound() {
        UUID userId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        User user = buildUser(userId);
        when(userService.findUser("user@example.com")).thenReturn(user);
        when(transactionRepository.findByIdAndUserId(txId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getById("user@example.com", txId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private User buildUser(UUID id) {
        User u = new User(); u.setId(id); u.setEmail("user@example.com");
        u.setName("User"); u.setPasswordHash("hash"); u.setPlan(UserPlan.BASIC);
        return u;
    }

    private Category buildCategory(UUID id, String name, User user) {
        Category c = new Category(); c.setId(id); c.setName(name);
        c.setIncomeEnabled(false); c.setExpenseEnabled(true); c.setUser(user); return c;
    }

    private Transaction buildTransaction(UUID id, TransactionType type, BigDecimal amount, User user) {
        Transaction t = new Transaction(); t.setId(id); t.setType(type);
        t.setAmount(amount); t.setUser(user); t.setDate(LocalDate.now()); return t;
    }
}
