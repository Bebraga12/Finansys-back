package com.finasys.transaction.repository;

import com.finasys.common.enums.TransactionType;
import com.finasys.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserId(UUID userId);
    List<Transaction> findByUserIdAndType(UUID userId, TransactionType type);
    List<Transaction> findByUserIdAndDateBetween(UUID userId, LocalDate start, LocalDate end);
    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
    boolean existsByIdAndUserId(UUID id, UUID userId);
}
