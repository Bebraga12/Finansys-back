package com.finasys.category.controller;

import com.finasys.category.dto.CategoryRequest;
import com.finasys.category.dto.CategoryResponse;
import com.finasys.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listAll(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(categoryService.listAll(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(userDetails.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getById(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable UUID id,
                                                    @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id) {
        categoryService.delete(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
