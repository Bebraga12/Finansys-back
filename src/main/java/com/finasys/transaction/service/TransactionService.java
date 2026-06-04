package com.finasys.transaction.service;

import com.finasys.category.dto.CategoryResponse;
import com.finasys.category.model.Category;
import com.finasys.category.repository.CategoryRepository;
import com.finasys.common.enums.TransactionType;
import com.finasys.common.exception.ResourceNotFoundException;
import com.finasys.transaction.dto.TransactionRequest;
import com.finasys.transaction.dto.TransactionResponse;
import com.finasys.transaction.model.Transaction;
import com.finasys.transaction.repository.TransactionRepository;
import com.finasys.user.model.User;
import com.finasys.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository,
                               CategoryRepository categoryRepository,
                               UserService userService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public List<TransactionResponse> list(String email, TransactionType type, UUID categoryId,
                                           LocalDate startDate, LocalDate endDate) {
        User user = userService.findUser(email);
        return transactionRepository.findByUserId(user.getId()).stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> categoryId == null || (t.getCategory() != null && t.getCategory().getId().equals(categoryId)))
                .filter(t -> startDate == null || !t.getDate().isBefore(startDate))
                .filter(t -> endDate == null || !t.getDate().isAfter(endDate))
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse create(String email, TransactionRequest request) {
        User user = userService.findUser(email);

        if (request.type() == TransactionType.EXPENSE && request.categoryId() == null) {
            throw new IllegalArgumentException("Categoria é obrigatória para despesas.");
        }

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setCategory(category);
        transaction.setDescription(request.description());
        transaction.setDate(request.date());

        return toResponse(transactionRepository.save(transaction));
    }

    public TransactionResponse getById(String email, UUID id) {
        User user = userService.findUser(email);
        return toResponse(findTransaction(id, user.getId()));
    }

    public TransactionResponse update(String email, UUID id, TransactionRequest request) {
        User user = userService.findUser(email);
        Transaction transaction = findTransaction(id, user.getId());

        if (request.type() == TransactionType.EXPENSE && request.categoryId() == null) {
            throw new IllegalArgumentException("Categoria é obrigatória para despesas.");
        }

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        }

        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setCategory(category);
        transaction.setDescription(request.description());
        transaction.setDate(request.date());

        return toResponse(transactionRepository.save(transaction));
    }

    public void delete(String email, UUID id) {
        User user = userService.findUser(email);
        Transaction transaction = findTransaction(id, user.getId());
        transactionRepository.delete(transaction);
    }

    private Transaction findTransaction(UUID id, UUID userId) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));
    }

    public TransactionResponse toResponse(Transaction t) {
        CategoryResponse categoryResponse = t.getCategory() != null
                ? new CategoryResponse(t.getCategory().getId(), t.getCategory().getName(),
                        t.getCategory().isIncomeEnabled(), t.getCategory().isExpenseEnabled())
                : null;
        return new TransactionResponse(t.getId(), t.getType(), t.getAmount(), categoryResponse, t.getDescription(), t.getDate());
    }
}
