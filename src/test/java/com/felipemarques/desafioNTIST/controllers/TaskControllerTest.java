package com.felipemarques.desafioNTIST.controllers;

import com.felipemarques.desafioNTIST.dtos.TaskRegisterDTO;
import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.models.Priority;
import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.services.TaskService;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    private Task task;
    private TaskRegisterDTO taskRegisterDTO;
    private final UUID TASK_ID = UUID.randomUUID();
    private final String TASK_DESCRIPTION = "description example";
    private final Priority TASK_PRIORITY = Priority.HIGH;
    private final Boolean TASK_COMPLETED = true;
    private final String TASK_NEW_DESCRIPTION = "new description";
    private final UUID USER_ID = UUID.randomUUID();
    private final String USER_NAME = "Vanessa Pereira Dias";
    private final String USER_FIRST_NAME = "Vanessa";
    private final String USER_EMAIL = "vanessa@gmail.com";
    private final String USER_PASSWORD = "Van#001";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        task = new Task(TASK_ID, TASK_DESCRIPTION, TASK_PRIORITY, TASK_COMPLETED, USER_ID);
        taskRegisterDTO = new TaskRegisterDTO(TASK_DESCRIPTION, TASK_PRIORITY);
        User user = new User(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD);

        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(user, user.getPassword(), user.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }

    @DisplayName("Given task list, when showTask(), then add model and return tasks_page")
    @Test
    void showTasksTest() {
        List<Task> taskList = List.of(task);
        when(taskService.findByUserId()).thenReturn(taskList);

        String viewName = taskController.showTasks(model);

        verify(model, times(1))
                .addAttribute(eq("firstUserName"), eq(USER_FIRST_NAME));
        verify(model, times(1))
                .addAttribute(eq("tasks"), eq(taskList));
        assertEquals("tasks_page", viewName);
    }

    @DisplayName("When showCreateTaskPage(), then add model and return create_task_page")
    @Test
    void showCreateTaskPageTest() {
        String viewName = taskController.showCreatTaskPage(model);

        verify(model, times(1))
                .addAttribute(eq("newTask"), any(TaskRegisterDTO.class));
        assertEquals("create_task_page", viewName);
    }

    @DisplayName("Given not erros, when create(), then call create of TaskService and redirect to /tasks")
    @Test
    void createTest() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String redirectResult = taskController.create(taskRegisterDTO, bindingResult);

        verify(taskService, times(1)).create(taskRegisterDTO);
        assertEquals("redirect:/tasks", redirectResult);
    }

    @DisplayName("Given errors, when create(), then return create_task_page")
    @Test
    void createTestCase2() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = taskController.create(taskRegisterDTO, bindingResult);

        assertEquals("create_task_page", viewName);
    }

    @DisplayName("Given task list, when showUncompletedTasks(), then add models and return uncompleted_tasks_page")
    @Test
    void showUncompletedTasks() {
        List<Task> taskList = List.of(task);
        when(taskService.findUncompletedTaskByUserId()).thenReturn(taskList);

        String viewName = taskController.showUncompletedTasks(model);

        verify(model, times(1))
                .addAttribute(eq("firstUserName"), eq(USER_FIRST_NAME));
        verify(model, times(1))
                .addAttribute(eq("tasks"), eq(taskList));
        assertEquals("uncompleted_tasks_page", viewName);
    }

    @DisplayName("Given task list, when showUncompletedTasksWithFilter(), then add models " +
            "and return uncompleted_tasks_page")
    @Test
    void showUncompletedTasksWithFilterTest() {
        List<Task> taskList = List.of(task);
        when(taskService.findUncompletedTaskWithFilter(TASK_PRIORITY)).thenReturn(taskList);

        String viewName = taskController.showUncompletedTasksWithFilter(TASK_PRIORITY, model);

        verify(model, times(1))
                .addAttribute(eq("firstUserName"), eq(USER_FIRST_NAME));
        verify(model, times(1))
                .addAttribute(eq("tasks"), eq(taskList));
        verify(model, times(1))
                .addAttribute(eq("filterValue"), eq(TASK_PRIORITY));
        assertEquals("uncompleted_tasks_page", viewName);
    }

    @DisplayName("Given TASK_ID, when delete(), then calls deleteById of TaskService and redirect to /tasks")
    @Test
    void deleteTest() {
        String redirectResult = taskController.delete(TASK_ID);

        verify(taskService, times(1)).deleteById(TASK_ID);
        assertEquals("redirect:/tasks", redirectResult);
    }

    @DisplayName("Given TASK_ID, when updateCompletedField(), then calls updateCompletedStatus() of TaskService " +
            "and redirect to /tasks")
    @Test
    void updateCompletedFieldTest() {
        String redirectResult = taskController.updateCompletedField(TASK_ID);

        verify(taskService, times(1)).updateCompletedStatus(TASK_ID);
        assertEquals("redirect:/tasks", redirectResult);
    }

    @DisplayName("Given tasks, when showUpdateTaskPage(), then add models and return update_task_page")
    @Test
    void showUpdateTaskPageTest() {
        when(taskService.findByIdAndUserId(TASK_ID)).thenReturn(task);

        String viewName = taskController.showUpdateTaskPage(TASK_ID, model);

        verify(model, times(1))
                .addAttribute(eq("id"), eq(TASK_ID));
        verify(model, times(1))
                .addAttribute(eq("task"), any(TaskRegisterDTO.class));
        assertEquals("update_task_page", viewName);
    }

    @DisplayName("Given not errors, when updateTaskDescriptionAndPriority(), then calls updateValues() of " +
            "TaskService and redirect to /tasks")
    @Test
    void updateTaskDescriptionAndPriorityTest() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String redirectResult = taskController
                .updateTaskDescriptionAndPriority(TASK_ID, taskRegisterDTO, bindingResult, model);

        verify(taskService, times(1))
                .updateValues(TASK_ID, TASK_DESCRIPTION, TASK_PRIORITY);
        assertEquals("redirect:/tasks", redirectResult);
    }

    @DisplayName("Given errors, when updateTaskDescriptionAndPriority(), then add model and return " +
            "to update_task_page")
    @Test
    void updateTaskDescriptionAndPriorityTestCase2() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewPage = taskController
                .updateTaskDescriptionAndPriority(TASK_ID, taskRegisterDTO, bindingResult, model);

        verify(model, times(1))
                .addAttribute(eq("id"), eq(TASK_ID));
        assertEquals("update_task_page", viewPage);
    }
}