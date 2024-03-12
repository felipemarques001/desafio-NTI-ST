package com.felipemarques.desafioNTIST.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO (

        @NotBlank(message = "The 'email' cannot be empty!")
        String email,
        @NotBlank(message = "The 'password' cannot be empty!")
        String password
){
}
