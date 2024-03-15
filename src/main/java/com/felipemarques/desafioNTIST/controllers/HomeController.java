package com.felipemarques.desafioNTIST.controllers;

import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.services.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final TaskService taskService;

    public HomeController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<Task> tasks = taskService.findByUserId();
        model.addAttribute("tasks", tasks);
        return "home_page";
    }
}
