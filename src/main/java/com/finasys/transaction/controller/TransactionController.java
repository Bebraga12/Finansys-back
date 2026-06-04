package com.finasys.transaction.controller;

import com.finasys.common.enums.TransactionType;
import com.finasys.transaction.dto.TransactionRequest;
import com.finasys.transaction.dto.TransactionResponse;
import com.finasys.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> list(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(transactionService.list(userDetails.getUsername(), type, categoryId, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(userDetails.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getById(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable UUID id,
                                                       @RequestBody @Valid TransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id) {
        transactionService.delete(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
