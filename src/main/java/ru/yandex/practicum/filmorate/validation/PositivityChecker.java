package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;

public class PositivityChecker {

    public static void checkForPositivity(Long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("Некорректно указан параметр - " + id + ". Должен быть больше нуля.");
        }
    }

    public static void checkForPositivityCount(Integer count) {
        if (count <= 0) {
            throw new IncorrectParameterException("Некорректно указан параметр - " + count + ". Должен быть больше нуля.");
        }
    }
}
