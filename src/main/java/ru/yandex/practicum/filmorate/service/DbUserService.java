package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoFriends;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.MissingFriendException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbUserService implements UserServiceInt {
    private final UserStorage userStorage;
    private final DaoFriends daoFriends;

    @Override
    public User saveUser(User user) {
        User validateUser = validUser(user);
        return userStorage.saveUser(validateUser).orElseThrow(() ->
                new DatabaseException("При записи пользователя в базу данных произошла ошибка"));
    }

    @Override
    public User getUserById(Long id) {
        checkForPositivity(id);
        return userStorage.getUserById(id).orElseThrow(() ->
                new DatabaseException("Пользователь в базе данных не найден"));
    }

    @Override
    public User updateUser(User user) {
        Optional<User> checkUser = userStorage.getUserById(user.getId());
        if (checkUser.isPresent()) {
            return userStorage.updateUser(user).orElseThrow(() ->
                    new DatabaseException("Произошла ошибка при обновлении пользователя"));
        } else {
            log.info("Ошибка нет пользователя для обновления");
            throw new DatabaseException("Невозможно обновить. Такой пользователь не найден.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        checkForPositivity(id);
        checkForPositivity(friendId);
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (user.getUnverifiedFriends().contains(friendId) || user.getFriends().contains(friendId)) {
            return user;
        }
        if (friend.getUnverifiedFriends().contains(id)) {
            daoFriends.deleteUnverifiedFriend(friendId, id);
            daoFriends.addFriend(friendId, id);
            daoFriends.addFriend(id, friendId);
        } else daoFriends.addUnverifiedFriend(id, friendId);
        return getUserById(id);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        checkForPositivity(id);
        return daoFriends.getFriends(id);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        checkForPositivity(id);
        checkForPositivity(otherId);
        return daoFriends.getCommonFriends(id, otherId);
    }

    @Override
    public User deleteFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (!user.getFriends().contains(friendId) && !user.getUnverifiedFriends().contains(friendId)) {
            throw new MissingFriendException(String.format(
                    "Ошибка. У пользователя %s нет друга - %s", user.getName(), friend.getName()));
        }
        if (user.getFriends().contains(friendId)) {
            daoFriends.deleteFriend(id, friendId);
            daoFriends.deleteFriend(friendId, id);
            daoFriends.addUnverifiedFriend(friendId, id);
        } else {
            daoFriends.deleteUnverifiedFriend(id, friendId);
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
