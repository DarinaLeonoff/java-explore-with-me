package ru.practicum.ewm.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        user = userRepository.save(User.builder().email("email@ya.ru").name("name name").build());
        pageable = PageRequest.of(0 / 10, 10, Sort.by("id").ascending());
    }

    @Test
    void findUsersTest() {
        User result = userRepository.findUsers(List.of(user.getId()), pageable).getFirst();

        assertEquals(user, result);
    }

    @Test
    void findUsersByWrongIdTest() {
        List<User> result = userRepository.findUsers(List.of(100L, 2000L), pageable);

        assertTrue(result.isEmpty());
    }


}
