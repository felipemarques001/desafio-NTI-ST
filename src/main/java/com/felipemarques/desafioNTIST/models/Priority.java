package com.felipemarques.desafioNTIST.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Priority {

    HIGH("high"),
    MEDIUM("medium"),
    LOW("low");

    private String value;
}
