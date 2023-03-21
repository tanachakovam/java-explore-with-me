package ru.practicum;

public class WrongEndTimeException extends RuntimeException {
    public WrongEndTimeException(String message) {
        super(message);
    }
}
