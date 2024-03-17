package com.felipemarques.desafioNTIST.repositories;

import com.felipemarques.desafioNTIST.models.Priority;
import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Given user and task, when savetask(), then save user")
    void saveTaskTest() {
        userRepository.save(user);

        int rowsAffected = taskRepository.save(task);
        assertEquals(1, rowsAffected);
    }

    @Test
    @DisplayName("Given user and task, when findByUserId(), then return tasks founded")
    void findByUserIdTest() {
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
    @DisplayName("Given TASK_ID, when deleteById(), then delete task")
    void deleteByIdTest(){
        userRepository.save(user);
        taskRepository.save(task);

        int rowsAffected = taskRepository.deleteById(TASK_ID);
        List<Task> tasksFounded = taskRepository.findByUserId(USER_ID);

        assertEquals(1, rowsAffected);
        assertEquals(0, tasksFounded.size());
    }

    @Test
    @DisplayName("Given !TASK_COMPLETED and task ID, when updateCompletedStatus(), then update completed value")
    void updateCompletedStatusTest() {
        userRepository.save(user);
        taskRepository.save(task);

        int rowsAffected = taskRepository.updateCompletedStatus(!TASK_COMPLETED, task.getId());
        List<Task> tasks = taskRepository.findByUserId(user.getId());

        assertEquals(1, rowsAffected);
        assertEquals(!TASK_COMPLETED, tasks.get(0).getCompleted());
    }

    @Test
    @DisplayName("Given user and task, when findByIdAndUserId(), then return task")
    void findByIdAndUserIdTest() {
        userRepository.save(user);
        taskRepository.save(task);

        Task taskFounded = taskRepository.findByIdAndUserId(task.getId(), user.getId());

        assertEquals(TASK_ID, taskFounded.getId());
        assertEquals(TASK_DESCRIPTION, taskFounded.getDescription());
        assertEquals(TASK_PRIORITY, taskFounded.getPriority());
        assertEquals(TASK_COMPLETED, taskFounded.getCompleted());
    }

    @Test
    @DisplayName("Not given task, when findByIdAndUserId(), then return null")
    void findByIdAndUserIdTestCase2() {
        Task taskFounded = taskRepository.findByIdAndUserId(task.getId(), user.getId());

        assertNull(taskFounded);
    }

    @Test
    @DisplayName("Given new description and priority, when updateDescriptionAndPriority(), then update values")
    void updateDescriptionAndPriorityTest() {
        userRepository.save(user);
        taskRepository.save(task);

        String newDescription = "new description";
        Priority newPriority = Priority.MEDIUM;

        int rowAffected = taskRepository.updateDescriptionAndPriority(TASK_ID,
                newDescription,
                newPriority);

        Task taskFounded = taskRepository.findByIdAndUserId(task.getId(), user.getId());

        assertEquals(1, rowAffected);
        assertEquals(newDescription, taskFounded.getDescription());
        assertEquals(newPriority, taskFounded.getPriority());
    }

    @Test
    @DisplayName("Given tasks, when findTasksUncompletedByUserId(), then return uncompleted tasks")
    void findTasksUncompletedByUserIdTest() {
        userRepository.save(user);
        taskRepository.save(task);

        for(int i = 0; i < 3; i++) {
            taskRepository.save(
                    new Task(UUID.randomUUID(),
                            TASK_DESCRIPTION,
                            Priority.HIGH,
                            false,
                            USER_ID)
            );
        }

        List<Task> tasks = taskRepository.findTasksUncompletedByUserId(USER_ID);

        assertEquals(3, tasks.size());
    }

    @Test
    @DisplayName("Given tasks, when findUncompletedTasksByUserIdAndPriority(), then return uncompleted filtered tasks")
    void findUncompletedTasksByUserIdAndPriorityTest() {
        userRepository.save(user);

        for(int i = 0; i < 3; i++) {
            taskRepository.save(
                    new Task(UUID.randomUUID(),
                            TASK_DESCRIPTION,
                            Priority.HIGH,
                            false,
                            USER_ID)
            );
        }
        for(int i = 0; i < 3; i++) {
            taskRepository.save(
                    new Task(UUID.randomUUID(),
                            TASK_DESCRIPTION,
                            Priority.MEDIUM,
                            false,
                            USER_ID)
            );
        }
        for(int i = 0; i < 3; i++) {
            taskRepository.save(
                    new Task(UUID.randomUUID(),
                            TASK_DESCRIPTION,
                            Priority.LOW,
                            false,
                            USER_ID)
            );
        }

        List<Task> tasks = taskRepository.findUncompletedTasksByUserIdAndPriority(USER_ID, Priority.HIGH);

        assertEquals(3, tasks.size());
    }
}