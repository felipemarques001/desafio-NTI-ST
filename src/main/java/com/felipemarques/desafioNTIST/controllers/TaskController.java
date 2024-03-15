package com.felipemarques.desafioNTIST.controllers;

import com.felipemarques.desafioNTIST.dtos.TaskRegisterDTO;
import com.felipemarques.desafioNTIST.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping
    public String showCreatTaskPage(Model model) {
        model.addAttribute("newTask", new TaskRegisterDTO());
        return "create_task_page";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("newTask") TaskRegisterDTO taskDto,
                       BindingResult result) {
        if(result.hasErrors()) {
            return "create_task_page";
        }
        taskService.create(taskDto);
        return "redirect:/home";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam UUID id) {
        taskService.deleteById(id);
        return "redirect:/home";
    }
}
