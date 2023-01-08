package ru.yandex.practicum.filmorate.exception;

public class MissingFilmException extends RuntimeException {
    public MissingFilmException(final String message) {
        super(message);
    }
}
