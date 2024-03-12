package com.felipemarques.desafioNTIST.exceptions;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }
}
