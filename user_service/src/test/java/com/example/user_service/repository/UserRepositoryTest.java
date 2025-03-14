package com.example.user_service.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.user_service.entity.User;

@DataJpaTest // JPA 관련된 테스트만 실행(쿼리가 예상대로 동작하는지 확인)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_Success() {
        // Given
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        userRepository.save(user);
        userRepository.flush();

        // When
        Optional<User> foundUser = userRepository.findByUsername("testUser");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
    }
}
