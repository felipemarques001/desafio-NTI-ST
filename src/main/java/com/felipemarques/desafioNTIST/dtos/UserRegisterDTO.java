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
public class UserRegisterDTO {

        @NotBlank(message = "The 'name' cannot be empty!")
        private String name;

        @NotBlank(message = "The 'email' cannot be empty!")
        private String email;

        @NotBlank(message = "The 'password' cannot be empty!")
        private String password;
}
