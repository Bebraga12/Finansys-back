package com.finasys.user.repository;

import com.finasys.common.enums.UserPlan;
import com.finasys.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindByEmail() {
        User user = buildUser("Ana", "ana@repo.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("ana@repo.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ana");
        assertThat(found.get().getEmail()).isEqualTo("ana@repo.com");
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("ghost@repo.com");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        userRepository.save(buildUser("Pedro", "pedro@repo.com"));
        assertThat(userRepository.existsByEmail("pedro@repo.com")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        assertThat(userRepository.existsByEmail("nobody@repo.com")).isFalse();
    }

    private User buildUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash("hash");
        user.setPlan(UserPlan.BASIC);
        return user;
    }
}
