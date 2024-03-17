package com.felipemarques.desafioNTIST.controllers;

import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.FieldAlreadyInUseException;
import com.felipemarques.desafioNTIST.exceptions.InvalidPasswordException;
import com.felipemarques.desafioNTIST.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Exibe a página de login",
            description = "Exibe a página **login_page.html** para o usuário realizar login"
    )
    @GetMapping("/login")
    public String showLoginPage() {
        return "login_page";
    }

    @Operation(
            summary = "Exibe a página de cadastro do usuário",
            description = "Exibe a página **register_page.html** para o usuário se cadastrar"
    )
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("newUser", new UserRegisterDTO());
        return "register_page";
    }

    @Operation(
            summary = "Salva uma novo User no banco de dados",
            description = "Salva o usuário no banco de dados e redireciona para **/login**"
    )
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("newUser") UserRegisterDTO userDTO,
                           BindingResult result,
                           Model model) {
        if(result.hasErrors()) {
            return "register_page";
        }

        try {
            userService.register(userDTO);
        } catch (InvalidPasswordException ex) {
            model.addAttribute("invalidPassword", ex.getMessage());
            return "register_page";
        } catch (FieldAlreadyInUseException ex) {
            model.addAttribute("emailInUse", ex.getMessage());
            return "register_page";
        }

        return "redirect:/login";
    }
}
