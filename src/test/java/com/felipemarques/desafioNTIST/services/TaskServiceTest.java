package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.TaskRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.TaskNotBelongToUserException;
import com.felipemarques.desafioNTIST.models.Priority;
import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    private TaskRegisterDTO taskRegisterDTO;
    private Task task;

    private final UUID TASK_ID = UUID.randomUUID();
    private final String TASK_DESCRIPTION = "description example";
    private final Priority TASK_PRIORITY = Priority.HIGH;
    private final Boolean TASK_COMPLETED = true;
    private final String TASK_NEW_DESCRIPTION = "new description";
    private final String MESSAGE_TASK_NOT_BELONG_USER_EXCEPTION = "A tarefa não pertence ao usuário logado!";
    private final UUID USER_ID = UUID.randomUUID();
    private final String USER_NAME = "Vanessa";
    private final String USER_EMAIL = "vanessa@gmail.com";
    private final String USER_PASSWORD = "Van#001";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User user = new User(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD);
        taskRegisterDTO = new TaskRegisterDTO(TASK_DESCRIPTION, TASK_PRIORITY);
        task = new Task(TASK_ID, TASK_DESCRIPTION, TASK_PRIORITY, TASK_COMPLETED, USER_ID);

        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }

    @Test
    @DisplayName("Given user and task, when create(), then call save() of TaskRepository")
    void createTest() {
        taskService.create(taskRegisterDTO);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Given task list, when findTaskByUserId(), then return tasks")
    void findTaskByUserIdTest() {
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
    @DisplayName("Given task, when deleteTaskById(). then call deleteById() of TaskRepository")
    void deleteTaskByIdTest() {
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(task);

        taskService.deleteById(TASK_ID);
        verify(taskRepository, times(1)).deleteById(TASK_ID);
    }

    @Test
    @DisplayName("Not given task, when deleteTaskById(). then throws TaskNotBelongToUserException")
    void deleteTaskByIdTestCase2() {
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(null);

        try {
            taskService.deleteById(TASK_ID);
        } catch (Exception ex) {
            assertEquals(TaskNotBelongToUserException.class, ex.getClass());
            assertEquals(MESSAGE_TASK_NOT_BELONG_USER_EXCEPTION, ex.getMessage());
        }
    }

    @Test
    @DisplayName("Given task, when updateCompletedValue(), then calls updateCompletedStatus() of TaskRepository")
    void updateCompletedValueTest() {
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(task);

        taskService.updateCompletedStatus(TASK_ID);

        verify(taskRepository, times(1)).updateCompletedStatus(!TASK_COMPLETED, TASK_ID);
    }

    @Test
    @DisplayName("Not given task, when updateCompletedValue(), then throws TaskNotBelongToUserException")
    void updateCompletedValueTestCase2() {
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(null);

        try {
            taskService.updateCompletedStatus(TASK_ID);
        } catch (Exception ex) {
            assertEquals(TaskNotBelongToUserException.class, ex.getClass());
            assertEquals(MESSAGE_TASK_NOT_BELONG_USER_EXCEPTION, ex.getMessage());
        }
    }

    @Test
    @DisplayName("Given TASK_ID and new values, when updateDescription(), then calls updateDescriptionAndPriority() of TaskRepository")
    void updateDescriptionTest() {
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(task);

        taskService.updateValues(TASK_ID, TASK_NEW_DESCRIPTION, Priority.MEDIUM);

        verify(taskRepository, times(1))
                .updateDescriptionAndPriority(TASK_ID, TASK_NEW_DESCRIPTION, Priority.MEDIUM);
    }

    @Test
    @DisplayName("Not given task, when updateDescription(), then throws TaskNotBelongToUserException")
    void updateDescriptionTestCase2() {
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(null);

        try {
            taskService.updateValues(TASK_ID, TASK_NEW_DESCRIPTION, Priority.MEDIUM);
        } catch (Exception ex) {
            assertEquals(TaskNotBelongToUserException.class, ex.getClass());
            assertEquals(MESSAGE_TASK_NOT_BELONG_USER_EXCEPTION, ex.getMessage());
        }
    }

    @Test
    @DisplayName("Given uncompleted task, when findUncompletedTaskByUserId(), then return uncompleted task list")
    void findUncompletedTaskByUserIdTest() {
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
    @DisplayName("Given uncompleted tasks, when findUncompletedTaskWithFilter(), then return uncompleted task list")
    void findUncompletedTaskWithFilterTest() {
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

    @Test
    @DisplayName("Given task, when findByIdAndUserId(), then return task founded")
    void findByIdAndUserIdTest() {
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID))
                .thenReturn(task);

        Task tasksReturned = taskService.findByIdAndUserId(TASK_ID);

        assertEquals(TASK_ID, tasksReturned.getId());
        assertEquals(TASK_DESCRIPTION, tasksReturned.getDescription());
        assertEquals(TASK_PRIORITY, tasksReturned.getPriority());
        assertEquals(TASK_COMPLETED, tasksReturned.getCompleted());
        assertEquals(USER_ID, tasksReturned.getUserId());
    }

    @Test
    @DisplayName("Not given task, when findByIdAndUserId(), then throws TaskNotBelongToUserException")
    void findByIdAndUserIdTestCase2() {
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID))
                .thenReturn(null);

        try {
            taskService.findByIdAndUserId(TASK_ID);
        } catch (Exception ex) {
            assertEquals(TaskNotBelongToUserException.class, ex.getClass());
            assertEquals(MESSAGE_TASK_NOT_BELONG_USER_EXCEPTION, ex.getMessage());
        }
    }
}