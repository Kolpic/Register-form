package org.example.exception;

public class InvalidUserInputData extends RuntimeException {

    public InvalidUserInputData(String message) {
        super(message);
    }
}
