package com.felipemarques.desafioNTIST.repositories;

import com.felipemarques.desafioNTIST.models.Priority;
import com.felipemarques.desafioNTIST.models.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(Task task) {
        String sql = "INSERT INTO TB_TASK (id, description, priority, user_id, completed) VALUES (?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                task.getId(),
                task.getDescription(),
                task.getPriority().getValue(),
                task.getUserId(),
                task.getCompleted()
        );
    }

    public List<Task> findByUserId(UUID userId) {
        String sql = "SELECT id, description, priority, completed, user_id FROM TB_TASK WHERE user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Task newTask = new Task();
            String savedPriority = rs.getString("priority");

            if(savedPriority.equals("high")) {
                newTask.setPriority(Priority.HIGH);
            } else if (savedPriority.equals("medium")) {
                newTask.setPriority(Priority.MEDIUM);
            } else {
                newTask.setPriority(Priority.LOW);
            }

            newTask.setId(rs.getObject("id", UUID.class));
            newTask.setDescription(rs.getString(("description")));
            newTask.setCompleted(rs.getBoolean("completed"));
            newTask.setUserId(rs.getObject("user_id", UUID.class));
            return newTask;
        }, userId);
    }

    public int deleteById(UUID id) {
        String sql = "DELETE FROM TB_TASK WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}