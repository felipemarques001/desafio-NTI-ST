package com.felipemarques.desafioNTIST.controllers;

import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.services.TaskService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    private final TaskService taskService;

    public HomeController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> namesUser = List.of(user.getName().split(" "));
        List<Task> tasks = taskService.findByUserId();

        model.addAttribute("firstUserName", namesUser.get(0));
        model.addAttribute("tasks", tasks);

        return "home_page";
    }
}
