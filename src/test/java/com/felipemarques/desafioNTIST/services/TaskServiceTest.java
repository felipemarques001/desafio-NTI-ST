package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.TaskRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.TaskNotBelongToUser;
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

import java.util.ArrayList;
import java.util.List;
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
    private Task task;

    private final UUID TASK_ID = UUID.randomUUID();
    private final String TASK_DESCRIPTION = "description example";
    private final Priority TASK_PRIORITY = Priority.HIGH;
    private final Boolean TASK_COMPLETED = true;
    private final UUID USER_ID = UUID.randomUUID();
    private final String USER_NAME = "Vanessa";
    private final String USER_EMAIL = "vanessa@gmail.com";
    private final String USER_PASSWORD = "Van#001";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD);
        taskRegisterDTO = new TaskRegisterDTO(TASK_DESCRIPTION, TASK_PRIORITY);
        task = new Task(TASK_ID, TASK_DESCRIPTION, TASK_PRIORITY, TASK_COMPLETED, USER_ID);
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

    @Test
    void givenUserAndTask_whenFindTaskByUserId_thenReturnTasks() {
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        when(taskRepository.findByUserId(USER_ID))
                .thenReturn(List.of(task));

        List<Task> tasksReturned = taskService.findByUserId();

        assertEquals(1, tasksReturned.size());
        assertEquals(TASK_ID, tasksReturned.get(0).getId());
        assertEquals(TASK_DESCRIPTION, tasksReturned.get(0).getDescription());
        assertEquals(TASK_PRIORITY, tasksReturned.get(0).getPriority());
        assertEquals(TASK_COMPLETED, tasksReturned.get(0).getCompleted());
        assertEquals(USER_ID, tasksReturned.get(0).getUserId());
    }

    @Test
    void givenTaskId_whenDeleteTaskById_thenCallDeleteMethod() {
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(task);

        taskService.deleteById(TASK_ID);
        verify(taskRepository, times(1)).deleteById(TASK_ID);
    }

    @Test
    void givenTaskAndUser_whenUpdateCompletedValue_thenUpdateCompletedField() {
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(task);

        taskService.updateCompletedValue(TASK_ID);

        verify(taskRepository, times(1)).updateCompletedStatus(!TASK_COMPLETED, TASK_ID);
    }

    @Test
    void givenNotTask_whenUpdateCompletedValue_thenThrowTaskNotBelongToUserException() {
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(null);

        try {
            taskService.updateCompletedValue(TASK_ID);
        } catch (Exception ex) {
            assertEquals(TaskNotBelongToUser.class, ex.getClass());
            assertEquals("A tarefa não pertence ao usuário logado!", ex.getMessage());
        }
    }

    @Test
    void givenIdAndNewValues_whenUpdateDescriptionAndPriorityValue_thenUpdateTask() {
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(task);

        taskService.updateDescriptionAndPriorityValue(TASK_ID,
                "New description",
                Priority.MEDIUM);

        verify(taskRepository, times(1))
                .updateDescriptionAndPriority(TASK_ID,
                "New description",
                Priority.MEDIUM);
    }

    @Test
    void givenNotTask_whenUpdateDescriptionAndPriorityValue_thenThrowTaskNotBelongToUserException() {
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(null);

        try {
            taskService.updateDescriptionAndPriorityValue(TASK_ID,
                    "New description",
                    Priority.MEDIUM);
        } catch (Exception ex) {
            assertEquals(TaskNotBelongToUser.class, ex.getClass());
            assertEquals("A tarefa não pertence ao usuário logado!", ex.getMessage());
        }
    }

    @Test
    void givenUncompletedTasks_whenFindUncompletedTaskByUserId_thenReturnTaskLis() {
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        task.setCompleted(false);
        List<Task> uncompletedTasks = List.of(task);

        when(taskRepository.findTasksUncompletedByUserId(USER_ID)).thenReturn(uncompletedTasks);

        List<Task> tasksReturned = taskService.findUncompletedTaskByUserId();

        verify(taskRepository, times(1)).findTasksUncompletedByUserId(USER_ID);
        assertEquals(1, tasksReturned.size());
        assertEquals(TASK_ID, tasksReturned.get(0).getId());
        assertEquals(TASK_DESCRIPTION, tasksReturned.get(0).getDescription());
        assertEquals(TASK_PRIORITY, tasksReturned.get(0).getPriority());
        assertEquals(false, tasksReturned.get(0).getCompleted());
        assertEquals(USER_ID, tasksReturned.get(0).getUserId());
    }

    @Test
    void givenTasks_whenFindUncompletedTaskWithFilter_thenReturnTaskList() {
        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        ArrayList<Task> uncompletedTasks = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            uncompletedTasks
                    .add(new Task(UUID.randomUUID(),
                            TASK_DESCRIPTION,
                            Priority.HIGH,
                            false,
                            USER_ID)
                    );
        }

        when(taskRepository.findUncompletedTasksByUserIdAndPriority(USER_ID, Priority.HIGH))
                .thenReturn(uncompletedTasks);

        List<Task> tasksReturned = taskService.findUncompletedTaskWithFilter(Priority.HIGH);

        verify(taskRepository, times(1))
                .findUncompletedTasksByUserIdAndPriority(USER_ID, Priority.HIGH);
        assertEquals(3, tasksReturned.size());
    }
}