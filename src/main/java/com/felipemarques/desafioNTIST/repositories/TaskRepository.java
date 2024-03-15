package com.felipemarques.desafioNTIST.repositories;

import com.felipemarques.desafioNTIST.models.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
