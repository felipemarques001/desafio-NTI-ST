package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.TaskRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.TaskNotBelongToUser;
import com.felipemarques.desafioNTIST.models.Priority;
import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.TaskRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void create(TaskRegisterDTO dto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Task newTask = new Task(UUID.randomUUID(),
                dto.getDescription(),
                dto.getPriority(),
                false,
                user.getId());

        taskRepository.save(newTask);
    }

    public List<Task> findByUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskRepository.findByUserId(user.getId());
    }

    public List<Task> findUncompletedTaskByUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskRepository.findTasksUncompletedByUserId(user.getId());
    }

    public List<Task> findUncompletedTaskWithFilter(Priority priority) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskRepository.findUncompletedTasksByUserIdAndPriority(user.getId(), priority);
    }

    public void deleteById(UUID taskId) {
        taskRepository.deleteById(taskId);
    }

    public void updateCompletedValue(UUID taskId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId());

        if(task == null) {
            throw new TaskNotBelongToUser("A tarefa não pertence ao usuário logado!");
        }

        taskRepository.updateCompletedStatus(!task.getCompleted(), taskId);
    }

    public void updateDescriptionAndPriorityValue(UUID id, String description, Priority priority) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Task task = taskRepository.findByIdAndUserId(id, user.getId());

        if(task == null) {
            throw new TaskNotBelongToUser("A tarefa não pertence ao usuário logado!");
        }

        taskRepository.updateDescriptionAndPriority(id, description, priority);
    }

    public Task findByIdAndUserId(UUID taskId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId());

        if(task == null) {
            throw new TaskNotBelongToUser("A tarefa não pertence ao usuário logado!");
        }

        return task;
    }
}
