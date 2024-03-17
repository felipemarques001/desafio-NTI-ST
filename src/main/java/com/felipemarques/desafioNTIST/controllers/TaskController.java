package com.felipemarques.desafioNTIST.controllers;

import com.felipemarques.desafioNTIST.dtos.TaskRegisterDTO;
import com.felipemarques.desafioNTIST.models.Priority;
import com.felipemarques.desafioNTIST.models.Task;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Exibe todas as Tasks do usuário",
            description = "Captura todas as tarefas do usuário e exibe a página **tasks_page.html** " +
                    "passando tais tarefas"
    )
    @GetMapping
    public String showTasks(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> namesUser = List.of(user.getName().split(" "));
        List<Task> tasks = taskService.findByUserId();

        model.addAttribute("firstUserName", namesUser.get(0));
        model.addAttribute("tasks", tasks);

        return "tasks_page";
    }

    @Operation(
            summary = "Exibe a página HTML para criar uma nova Task",
            description = "Exibe a página **create_task_page.html**"
    )
    @GetMapping("/create")
    public String showCreatTaskPage(Model model) {
        model.addAttribute("newTask", new TaskRegisterDTO());
        return "create_task_page";
    }

    @Operation(
            summary = "Salva uma nova Task no banco de dados",
            description = "Salva a tarefa no banco de dados e redireciona para **/tasks**"
    )
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("newTask") TaskRegisterDTO taskDto,
                       BindingResult result) {
        if(result.hasErrors()) {
            return "create_task_page";
        }
        taskService.create(taskDto);
        return "redirect:/tasks";
    }

    @Operation(
            summary = "Exibe todas as Task's incompletas do usuário",
            description = "Captura todas as tarefas incompletas do usuário e exibe a página " +
                    "**uncompleted_tasks_page.html** passando tais tarefas"
    )
    @GetMapping("/uncompletedTasks")
    public String showUncompletedTasks(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> namesUser = List.of(user.getName().split(" "));
        List<Task> tasks = taskService.findUncompletedTaskByUserId();

        model.addAttribute("firstUserName", namesUser.get(0));
        model.addAttribute("tasks", tasks);

        return "uncompleted_tasks_page";
    }

    @Operation(
            summary = "Exibe todas as Task's incompletas do usuário com filtro de prioridade",
            description = "Captura todas as tarefas incompletas do usuário com prioridade igual a passada no " +
                    "parâmetro e exibe a página **uncompleted_tasks_page.html** passando tais tarefas"
    )
    @GetMapping("/uncompletedTasksWithFilter")
    public String showUncompletedTasksWithFilter(@RequestParam Priority priority, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> namesUser = List.of(user.getName().split(" "));
        List<Task> tasks = taskService.findUncompletedTaskWithFilter(priority);

        model.addAttribute("firstUserName", namesUser.get(0));
        model.addAttribute("tasks", tasks);
        model.addAttribute("filterValue", priority);

        return "uncompleted_tasks_page";
    }

    @Operation(
            summary = "Apaga uma Task",
            description = "Apaga a tarefa com ID igual ao passado no parâmetro e redireciona para **/tasks**"
    )
    @GetMapping("/delete")
    public String delete(@RequestParam UUID id) {
        taskService.deleteById(id);
        return "redirect:/tasks";
    }

    @Operation(
            summary = "Atualiza o valor de 'completed' de uma Task",
            description = "Atualiza o campo **completed** da tarefa com ID igual ao valor " +
                    "passado no parâmetro e redireciona para **/tasks**"
    )
    @GetMapping("/update_completed_field")
    public String updateCompletedField(@RequestParam UUID id) {
        taskService.updateCompletedStatus(id);
        return "redirect:/tasks";
    }

    @Operation(
            summary = "Exibi a página HTML para atualizar uma Task",
            description = "Captura os dados da tarefa com ID igual ao passado no parâmetro e exibe a página" +
                    " **update_task_page.html** passando a tarefa"
    )
    @GetMapping("/update")
    public String showUpdateTaskPage(@RequestParam UUID id, Model model) {
        Task task = taskService.findByIdAndUserId(id);
        TaskRegisterDTO taskDto =
                new TaskRegisterDTO(task.getDescription(), task.getPriority());

        model.addAttribute("id", id);
        model.addAttribute("task", taskDto);

        return "update_task_page";
    }

    @Operation(
            summary = "Atualiza os valores de 'description' e 'priority' de uma Task",
            description = "Atualiza o campo **description** e **priority** da tarefa com ID igual ao valor " +
                    "passado no parâmetro e redireciona para **/tasks**"
    )
    @PostMapping("/update")
    public String updateTaskDescriptionAndPriority(@RequestParam UUID id,
                                                   @Valid @ModelAttribute("task") TaskRegisterDTO taskDto,
                                                   BindingResult result,
                                                   Model model) {
        if(result.hasErrors()) {
            model.addAttribute("id", id);
            return "update_task_page";
        }

        taskService.updateValues(id,
                taskDto.getDescription(),
                taskDto.getPriority());

        return "redirect:/tasks";
    }
}