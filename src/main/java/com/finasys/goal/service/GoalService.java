package com.finasys.goal.service;

import com.finasys.common.exception.ResourceNotFoundException;
import com.finasys.goal.dto.GoalDepositRequest;
import com.finasys.goal.dto.GoalRequest;
import com.finasys.goal.dto.GoalResponse;
import com.finasys.goal.model.Goal;
import com.finasys.goal.repository.GoalRepository;
import com.finasys.user.model.User;
import com.finasys.user.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserService userService;

    public GoalService(GoalRepository goalRepository, UserService userService) {
        this.goalRepository = goalRepository;
        this.userService = userService;
    }

    public List<GoalResponse> listAll(String email) {
        User user = userService.findUser(email);
        return goalRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse).toList();
    }

    public GoalResponse create(String email, GoalRequest request) {
        User user = userService.findUser(email);
        Goal goal = new Goal();
        goal.setUser(user);
        goal.setTitle(request.title());
        goal.setTargetAmount(request.targetAmount());
        goal.setCurrentAmount(request.currentAmount());
        goal.setDeadline(request.deadline());
        return toResponse(goalRepository.save(goal));
    }

    public GoalResponse getById(String email, UUID id) {
        User user = userService.findUser(email);
        return toResponse(findGoal(id, user.getId()));
    }

    public GoalResponse update(String email, UUID id, GoalRequest request) {
        User user = userService.findUser(email);
        Goal goal = findGoal(id, user.getId());
        goal.setTitle(request.title());
        goal.setTargetAmount(request.targetAmount());
        goal.setCurrentAmount(request.currentAmount());
        goal.setDeadline(request.deadline());
        return toResponse(goalRepository.save(goal));
    }

    public GoalResponse deposit(String email, UUID id, GoalDepositRequest request) {
        User user = userService.findUser(email);
        Goal goal = findGoal(id, user.getId());
        BigDecimal newAmount = goal.getCurrentAmount().add(request.amount());
        goal.setCurrentAmount(newAmount);
        return toResponse(goalRepository.save(goal));
    }

    public void delete(String email, UUID id) {
        User user = userService.findUser(email);
        Goal goal = findGoal(id, user.getId());
        goalRepository.delete(goal);
    }

    private Goal findGoal(UUID id, UUID userId) {
        return goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada."));
    }

    private GoalResponse toResponse(Goal goal) {
        double progress = goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0 ? 0.0
                : goal.getCurrentAmount()
                    .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .doubleValue();
        return new GoalResponse(goal.getId(), goal.getTitle(), goal.getTargetAmount(),
                goal.getCurrentAmount(), goal.getDeadline(), progress);
    }
}
