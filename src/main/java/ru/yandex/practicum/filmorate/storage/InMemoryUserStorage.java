package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.MissingFriendException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Data
@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Long idUser = 1L;
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        User validateUser = validUser(user);
        validateUser.setId(idUser);
        users.put(idUser++, validateUser);
        return validateUser;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            User validateUser = validUser(user);
            users.put(validateUser.getId(), validateUser);
            return validateUser;
        } else {
            throw new MissingFriendException("Пользователя с таким id нет.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        if (users.get(id) == null) {
            throw new MissingFriendException(String.format(
                    "Ошибка. Пользователя с id - %s не найдено.", id));
        }
        return users.get(id);
    }

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
}
