package com.felipemarques.desafioNTIST.repositories;

import com.felipemarques.desafioNTIST.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserRepositoryTest {

    @InjectMocks
    private UserRepository repository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(UUID.randomUUID(), "Felipe", "felipe@gmail.com", "123");
    }

    @DisplayName("Unit test for save method")
    @Test
    void givenUser_whenSave_thenReturnSavedUser() {
        // given
        when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenReturn(1);

        // when
        int rowsModified = repository.save(user);

        // then
        assertEquals(1, rowsModified);
        verify(jdbcTemplate, times(1)).update(anyString(), any(Object[].class));
    }



//    @DisplayName("Unit test for findByEmail method")
//    @Test
//    void findByEmail() {
//        // given
//        repository.save(user);
//
//        // when
//        User user = (User) repository.findByEmail("felipe@gmail.com");
//
//        // then
//        assertEquals("Felipe", user.getName());
//        assertEquals("felipe@gmail.com", user.getEmail());
//        assertEquals("123", user.getPassword());
//    }
}