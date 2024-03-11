package com.felipemarques.desafioNTIST.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterDTO (
        @NotBlank(message = "The 'name' cannot be empty!")
        String name,

        @NotBlank(message = "The 'email' cannot be empty!")
        String email,

        @NotBlank(message = "The 'password' cannot be empty!")
        String password
){
}
