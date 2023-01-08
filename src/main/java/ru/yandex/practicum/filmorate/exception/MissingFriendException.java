package ru.yandex.practicum.filmorate.exception;

public class MissingFriendException extends RuntimeException {
    public MissingFriendException(final String message) {
        super(message);
    }
}
