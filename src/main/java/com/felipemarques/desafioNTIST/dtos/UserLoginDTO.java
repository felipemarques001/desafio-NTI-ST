package com.felipemarques.desafioNTIST.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO{

        @NotBlank(message = "The 'email' cannot be empty!")
        private String email;

        @NotBlank(message = "The 'password' cannot be empty!")
        private String password;
}
