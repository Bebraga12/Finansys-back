package com.finasys.goal.controller;

import com.finasys.goal.dto.GoalDepositRequest;
import com.finasys.goal.dto.GoalRequest;
import com.finasys.goal.dto.GoalResponse;
import com.finasys.goal.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> listAll(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(goalService.listAll(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<GoalResponse> create(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestBody @Valid GoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.create(userDetails.getUsername(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getById(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable UUID id) {
        return ResponseEntity.ok(goalService.getById(userDetails.getUsername(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> update(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable UUID id,
                                                @RequestBody @Valid GoalRequest request) {
        return ResponseEntity.ok(goalService.update(userDetails.getUsername(), id, request));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<GoalResponse> deposit(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable UUID id,
                                                 @RequestBody @Valid GoalDepositRequest request) {
        return ResponseEntity.ok(goalService.deposit(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id) {
        goalService.delete(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
