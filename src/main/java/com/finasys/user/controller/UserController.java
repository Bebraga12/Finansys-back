package com.finasys.user.controller;

import com.finasys.user.dto.UpdateUserRequest;
import com.finasys.user.dto.UserResponse;
import com.finasys.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMe(userDetails.getUsername()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateMe(userDetails.getUsername(), request));
    }
}
