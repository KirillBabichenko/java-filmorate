package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoUserService;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.MissingFriendException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;

@Service
@RequiredArgsConstructor
@Slf4j
public class DbUserService implements UserStorage {
    private final DaoUserService daoUserService;

    @Override
    public User createUser(User user) {
        User validateUser = validUser(user);
        Optional<User> userOptional = daoUserService.saveUser(validateUser);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else log.info("При записи пользователя в базу данных произошла ошибка");
        throw new DatabaseException("При записи пользователя в базу данных произошла ошибка");
    }

    @Override
    public User getUserById(Long id) {
        checkForPositivity(id);
        Optional<User> userOptional = daoUserService.getUserById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else log.info("Пользователь с id = {} в базе данных не найден", id);
        throw new DatabaseException("Пользователь в базе данных не найден");
    }

    @Override
    public User updateUser(User user) {
        Optional<User> checkUser = daoUserService.getUserById(user.getId());
        if (checkUser.isPresent()) {
            Optional<User> userOptional = daoUserService.updateUser(user);
            if (userOptional.isPresent()) {
                return userOptional.get();
            } else log.info("Произошла ошибка при обновлении пользователя");
            throw new DatabaseException("Произошла ошибка при обновлении пользователя");
        } else {
            log.info("Ошибка нет пользователя для обновления");
            throw new DatabaseException("Невозможно обновить. Такой пользователь не найден.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return daoUserService.getAllUsers();
    }

    public User addFriend(Long id, Long friendId) {
        checkForPositivity(id);
        checkForPositivity(friendId);
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (user.getUnverifiedFriends().contains(friendId) || user.getFriends().contains(friendId)) {
            return user;
        }
        if (friend.getUnverifiedFriends().contains(id)) {
            daoUserService.deleteUnverifiedFriend(friendId, id);
            daoUserService.addFriend(friendId, id);
            daoUserService.addFriend(id, friendId);
        } else daoUserService.addUnverifiedFriend(id, friendId);
        return getUserById(id);
    }

    public Collection<User> getFriends(Long id) {
        checkForPositivity(id);
        return daoUserService.getFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        checkForPositivity(id);
        checkForPositivity(otherId);
        return daoUserService.getCommonFriends(id, otherId);
    }

    public User deleteFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (!user.getFriends().contains(friendId) && !user.getUnverifiedFriends().contains(friendId)) {
            throw new MissingFriendException(String.format(
                    "Ошибка. У пользователя %s нет друга - %s", user.getName(), friend.getName()));
        }
        if (user.getFriends().contains(friendId)) {
            daoUserService.deleteFriend(id, friendId);
            daoUserService.deleteFriend(friendId, id);
            daoUserService.addUnverifiedFriend(friendId, id);
        } else {
            daoUserService.deleteUnverifiedFriend(id, friendId);
        }
        return getUserById(id);
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
