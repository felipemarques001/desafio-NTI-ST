package com.felipemarques.desafioNTIST.exceptions;

public class TaskNotBelongToUserException extends RuntimeException {
    public TaskNotBelongToUserException(String message) {
        super(message);
    }
}
