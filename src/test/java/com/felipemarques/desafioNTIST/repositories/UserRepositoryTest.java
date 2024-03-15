package com.felipemarques.desafioNTIST.repositories;

import com.felipemarques.desafioNTIST.models.User;
import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private final UUID ID = UUID.randomUUID();
    private final String NAME = "Vanessa";
    private final String EMAIL = "vanessa@gmail.com";
    private final String PASSWORD = "Van#001";

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM TB_USER");
        user = new User(ID, NAME, EMAIL, PASSWORD);
    }

    @Test
    void givenUser_whenSave_thenReturnOneRowAffected() {
        int rowsAffected = userRepository.save(user);
        assertEquals(1, rowsAffected);
    }

    @Test
    void givenSavedUser_whenFindByEmail_thenReturnSavedUser() {
        userRepository.save(user);
        User userFounded = (User) userRepository.findByEmail(EMAIL);

        assertEquals(ID, userFounded.getId());
        assertEquals(NAME, userFounded.getName());
        assertEquals(EMAIL, userFounded.getEmail());
        assertEquals(PASSWORD, userFounded.getPassword());
    }
}