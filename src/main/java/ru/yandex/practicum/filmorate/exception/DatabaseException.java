package ru.yandex.practicum.filmorate.exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(final String message) {
        super(message);
    }
}
