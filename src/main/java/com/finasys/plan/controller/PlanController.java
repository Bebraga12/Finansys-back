package com.finasys.plan.controller;

import com.finasys.plan.dto.PlanInfoResponse;
import com.finasys.plan.dto.UpdatePlanRequest;
import com.finasys.plan.service.PlanService;
import com.finasys.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<PlanInfoResponse>> listAll() {
        return ResponseEntity.ok(planService.listAll());
    }

    @GetMapping("/current")
    public ResponseEntity<PlanInfoResponse> getCurrent(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(planService.getCurrent(userDetails.getUsername()));
    }

    @PatchMapping("/current")
    public ResponseEntity<UserResponse> updatePlan(@AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestBody @Valid UpdatePlanRequest request) {
        return ResponseEntity.ok(planService.updatePlan(userDetails.getUsername(), request));
    }
}
