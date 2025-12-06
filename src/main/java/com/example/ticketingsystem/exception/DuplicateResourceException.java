package com.example.ticketingsystem.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String field, String value) {
        super(String.format("%s '%s' already exists", field, value));
    }
}
