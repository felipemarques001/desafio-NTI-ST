package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.TaskRegisterDTO;
import com.felipemarques.desafioNTIST.models.Priority;
import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    private User user;
    private TaskRegisterDTO taskRegisterDTO;

    private final String TASK_DESCRIPTION = "description example";
    private final Priority TASK_PRIORITY = Priority.HIGH;
    private final UUID USER_ID = UUID.randomUUID();
    private final String USER_NAME = "Vanessa";
    private final String USER_EMAIL = "vanessa@gmail.com";
    private final String USER_PASSWORD = "Van#001";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskRegisterDTO = new TaskRegisterDTO(TASK_DESCRIPTION, TASK_PRIORITY);
        user = new User(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD);
    }

    @Test
    void givenUserAndTask_whenCreate_thenCallTaskRepository() {
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        taskService.create(taskRegisterDTO);

        verify(taskRepository, times(1)).save(any(Task.class));
    }
}