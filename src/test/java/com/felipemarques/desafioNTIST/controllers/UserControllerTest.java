package com.felipemarques.desafioNTIST.controllers;

import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.FieldAlreadyInUseException;
import com.felipemarques.desafioNTIST.exceptions.InvalidPasswordException;
import com.felipemarques.desafioNTIST.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    private UserRegisterDTO userRegisterDTO;

    private final String NAME = "Vanessa";
    private final String EMAIL = "vanessa@gmail.com";
    private final String PASSWORD = "Van#001";
    private final String INVALID_PASSWORD_EXCEPTION_MESSAGE = "The password must contain at least one uppercase letter," +
            " one lowercase letter, one number, and one special character.";
    private final String FIELD_ALREADY_IN_USE_EXCEPTION_MESSAGE = "Error in the field 'email', the value '"
            + EMAIL + "' is already in use!";
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRegisterDTO = new UserRegisterDTO(NAME, EMAIL, PASSWORD);
    }

    @DisplayName("When calls showLoginPage() then return login_page")
    @Test
    void showLoginPageTest() {
        String viewName = userController.showLoginPage();

        assertEquals("login_page", viewName);
    }

    @DisplayName("Given model, when showRegisterPage(), add model and return register_page")
    @Test
    void showRegisterPageTest() {
        String viewName = userController.showRegisterPage(model);

        assertEquals("register_page", viewName);
        verify(model, times(1))
                .addAttribute(eq("newUser"), any(UserRegisterDTO.class));
    }

    @DisplayName("Given not errors, when registerTest(), then calls register() of UserService and redirect to /login")
    @Test
    void registerTest() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String redirectResult = userController.register(userRegisterDTO, bindingResult, model);

        verify(userService, times(1)).register(userRegisterDTO);
        assertEquals("redirect:/login", redirectResult);
    }

    @DisplayName("Given errors, when registerTest(), then return register_page")
    @Test
    void registerTestCase2() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String redirectResult = userController.register(userRegisterDTO, bindingResult, model);

        assertEquals("register_page", redirectResult);
    }

    @DisplayName("Given InvalidPasswordException, when registerTest(), " +
            "then addAtribute() of Model and return register_page")
    @Test
    void registerTestCase3() {
        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new InvalidPasswordException(INVALID_PASSWORD_EXCEPTION_MESSAGE))
                .when(userService)
                .register(userRegisterDTO);


        String viewName = userController.register(userRegisterDTO, bindingResult, model);

        verify(model, times(1))
                .addAttribute(eq("invalidPassword"), eq(INVALID_PASSWORD_EXCEPTION_MESSAGE));
        assertEquals(viewName, "register_page");
    }

    @DisplayName("Given FieldAlreadyInUseException, when registerTest(), " +
            "then addAtribute() of Model and return register_page")
    @Test
    void registerTestCase4() {
        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new FieldAlreadyInUseException("email", EMAIL))
                .when(userService)
                .register(userRegisterDTO);

        String viewName = userController.register(userRegisterDTO, bindingResult, model);

        verify(model, times(1))
                .addAttribute(eq("emailInUse"), eq(FIELD_ALREADY_IN_USE_EXCEPTION_MESSAGE));
        assertEquals(viewName, "register_page");
    }
}