package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@Getter
@Setter
@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private int idUser = 1;
    private final HashMap<Integer, User> users = new HashMap<>();

    private User validUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.info("Ошибка с логином. - {}", user);
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        User validateUser = validUser(user);
        validateUser.setId(idUser);
        users.put(idUser++, validateUser);
        return validateUser;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            User validateUser = validUser(user);
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
