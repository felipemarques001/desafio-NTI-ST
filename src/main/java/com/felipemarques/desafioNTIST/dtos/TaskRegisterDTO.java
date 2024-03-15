package com.felipemarques.desafioNTIST.dtos;

import com.felipemarques.desafioNTIST.models.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRegisterDTO {

    @NotBlank(message = "A descrição não pode ser vazia!")
    @Length(max = 255, message = "A descrição está muito grande!")
    private String description;

    @NotNull(message = "Por favor, defina uma prioridade!")
    private Priority priority;
}
