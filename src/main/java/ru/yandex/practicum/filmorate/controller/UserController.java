package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private int idUser = 1;
    private final HashMap<Integer, User> users = new HashMap<>();

    private User validateUser(User user) {
        if (user.getEmail().isBlank()) {
            log.info("Ошибка с электронной почтой. Она пуста- {}", user);
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.info("Ошибка с электронной почтой. Нет символа @ - {}", user);
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("Ошибка с логином. - {}", user);
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Ошибка с датой рождения - {}", user);
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        return user;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        User validateUser = validateUser(user);
        validateUser.setId(idUser);
        users.put(idUser++, validateUser);
        return validateUser;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            User validateUser = validateUser(user);
            users.put(validateUser.getId(), validateUser);
            return validateUser;
        } else {
            throw new ValidationException("Пользователя с таким id нет.");
        }
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return users.values();
    }
}
