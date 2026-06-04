package com.finasys.plan.service;

import com.finasys.common.enums.UserPlan;
import com.finasys.common.exception.InsufficientPlanException;
import com.finasys.plan.dto.PlanInfoResponse;
import com.finasys.plan.dto.UpdatePlanRequest;
import com.finasys.user.dto.UserResponse;
import com.finasys.user.model.User;
import com.finasys.user.repository.UserRepository;
import com.finasys.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final UserService userService;
    private final UserRepository userRepository;

    public PlanService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<PlanInfoResponse> listAll() {
        return List.of(
                new PlanInfoResponse(UserPlan.BASIC, "Basic",
                        List.of("Login", "Cadastro", "Dashboard básico", "Receitas", "Despesas",
                                "Categorias", "Metas ilimitadas", "Relatório mensal básico")),
                new PlanInfoResponse(UserPlan.PREMIUM, "Premium",
                        List.of("Tudo do Basic", "Dashboard completo", "Todos os gráficos",
                                "Relatórios completos", "Resumos financeiros detalhados")),
                new PlanInfoResponse(UserPlan.PREMIUM_PLUS, "Premium+",
                        List.of("Tudo do Premium", "Comparação entre meses",
                                "Insights automáticos", "Alertas de gastos", "Análises avançadas"))
        );
    }

    public PlanInfoResponse getCurrent(String email) {
        User user = userService.findUser(email);
        return listAll().stream()
                .filter(p -> p.plan() == user.getPlan())
                .findFirst()
                .orElseThrow();
    }

    public UserResponse updatePlan(String email, UpdatePlanRequest request) {
        User user = userService.findUser(email);
        user.setPlan(request.plan());
        return userService.toResponse(userRepository.save(user));
    }

    public void requirePlan(User user, UserPlan required) {
        if (user.getPlan().ordinal() < required.ordinal()) {
            throw new InsufficientPlanException();
        }
    }
}
