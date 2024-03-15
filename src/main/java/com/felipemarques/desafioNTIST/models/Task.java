package com.felipemarques.desafioNTIST.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    private UUID id;
    private String description;
    private Priority priority;
    private Boolean completed;
    private UUID userId;
}
