package com.felipemarques.desafioNTIST.exceptions;

public class TaskNotBelongToUser extends RuntimeException {
    public TaskNotBelongToUser(String message) {
        super(message);
    }
}
