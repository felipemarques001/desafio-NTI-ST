package com.felipemarques.desafioNTIST.controllers;

import com.felipemarques.desafioNTIST.dtos.TaskRegisterDTO;
import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping("/create")
    public String showCreatTaskPage(Model model) {
        model.addAttribute("newTask", new TaskRegisterDTO());
        return "create_task_page";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("newTask") TaskRegisterDTO taskDto,
                       BindingResult result) {
        if(result.hasErrors()) {
            return "create_task_page";
        }
        taskService.create(taskDto);
        return "redirect:/home";
    }

    @GetMapping("/uncompletedTasks")
    public String showUncompletedTasks(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> namesUser = List.of(user.getName().split(" "));
        List<Task> tasks = taskService.findUncompletedTaskByUserId();

        model.addAttribute("firstUserName", namesUser.get(0));
        model.addAttribute("tasks", tasks);

        return "uncompleted_tasks_page";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam UUID id) {
        taskService.deleteById(id);
        return "redirect:/home";
    }

    @GetMapping("/update_completed_field")
    public String updateCompletedField(@RequestParam UUID id) {
        taskService.updateCompletedValue(id);
        return "redirect:/home";
    }

    @GetMapping("/update")
    public String showUpdateTaskPage(@RequestParam UUID id, Model model) {
        Task task = taskService.findByIdAndUserId(id);
        TaskRegisterDTO taskDto =
                new TaskRegisterDTO(task.getDescription(), task.getPriority());

        model.addAttribute("id", id);
        model.addAttribute("task", taskDto);

        return "update_task_page";
    }

    @PostMapping("/update")
    public String updateTaskDescriptionAndPriority(@RequestParam UUID id,
                                                   @Valid @ModelAttribute("task") TaskRegisterDTO taskDto,
                                                   BindingResult result,
                                                   Model model) {
        if(result.hasErrors()) {
            model.addAttribute("id", id);
            return "update_task_page";
        }

        taskService.updateDescriptionAndPriorityValue(id,
                taskDto.getDescription(),
                taskDto.getPriority());

        return "redirect:/home";
    }
}