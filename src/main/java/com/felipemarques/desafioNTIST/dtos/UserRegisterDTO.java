package com.felipemarques.desafioNTIST.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {

        @NotBlank(message = "O nome não pode ser vazio!")
        @Length(max = 60, message = "Insira um nome de até 60 caracteres!")
        private String name;

        @NotBlank(message = "O e-mail não pode ser vazio!")
        @Email(message = "Por favor, insira um e-mail válido!")
        private String email;

        @NotBlank(message = "A senha não pode ser vazia!")
        @Length(max = 15, message = "Insira uma senha de até 15 caracteres!")
        private String password;
}
