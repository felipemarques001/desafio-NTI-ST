package com.felipemarques.desafioNTIST.repositories;

import com.felipemarques.desafioNTIST.models.Priority;
import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private Task task;
    private User user;

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
        jdbcTemplate.execute("DELETE FROM TB_TASK");
        jdbcTemplate.execute("DELETE FROM TB_USER");
        task = new Task(TASK_ID, TASK_DESCRIPTION, TASK_PRIORITY, TASK_COMPLETED, USER_ID);
        user = new User(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD);
    }

    @Test
    void givenUserAndTask_whenSaveTask_thenReturnOneRowAffected() {
        userRepository.save(user);

        int rowsAffected = taskRepository.save(task);
        assertEquals(1, rowsAffected);
    }

    @Test
    void givenUserAndTasks_whenFindByUserId_thenReturnTasks() {
        userRepository.save(user);
        taskRepository.save(task);

        task.setId(UUID.randomUUID());
        task.setPriority(Priority.MEDIUM);
        taskRepository.save(task);

        task.setId(UUID.randomUUID());
        task.setPriority(Priority.LOW);
        taskRepository.save(task);

        List<Task> tasksFounded = taskRepository.findByUserId(user.getId());

        assertEquals(3, tasksFounded.size());
        assertEquals(TASK_ID, tasksFounded.get(0).getId());
        assertEquals(TASK_DESCRIPTION, tasksFounded.get(0).getDescription());
        assertEquals(TASK_PRIORITY, tasksFounded.get(0).getPriority());
        assertEquals(TASK_COMPLETED, tasksFounded.get(0).getCompleted());
        assertEquals(USER_ID, tasksFounded.get(0).getUserId());

        assertEquals(Priority.MEDIUM, tasksFounded.get(1).getPriority());
        assertEquals(Priority.LOW, tasksFounded.get(2).getPriority());
    }

    @Test
    void givenTaskId_whenDeleteById_thenDeleteSuccessfully(){
        userRepository.save(user);
        taskRepository.save(task);

        int rowsAffected = taskRepository.deleteById(TASK_ID);
        List<Task> tasksFounded = taskRepository.findByUserId(USER_ID);

        assertEquals(1, rowsAffected);
        assertEquals(0, tasksFounded.size());
    }
}