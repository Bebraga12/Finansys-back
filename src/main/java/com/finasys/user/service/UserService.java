package com.finasys.user.service;

import com.finasys.common.exception.ResourceNotFoundException;
import com.finasys.user.dto.UpdateUserRequest;
import com.finasys.user.dto.UserResponse;
import com.finasys.user.model.User;
import com.finasys.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getMe(String email) {
        return toResponse(findUser(email));
    }

    public UserResponse updateMe(String email, UpdateUserRequest request) {
        User user = findUser(email);
        user.setName(request.name());
        return toResponse(userRepository.save(user));
    }

    public User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPlan(), user.getCreatedAt());
    }
}
