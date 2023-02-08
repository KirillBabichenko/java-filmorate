package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoFriendsRepository;
import ru.yandex.practicum.filmorate.dao.DaoUserRepository;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.MissingFriendException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbUserService implements UserServiceInt {
    private final DaoUserRepository daoUserRepository;
    private final DaoFriendsRepository daoFriendsRepository;

    @Override
    public User saveUser(User user) {
        User validateUser = validUser(user);
        return daoUserRepository.saveUser(validateUser).orElseThrow(() ->
                new DatabaseException("При записи пользователя в базу данных произошла ошибка"));
    }

    @Override
    public User getUserById(Long id) {
        checkForPositivity(id);
        return daoUserRepository.getUserById(id).orElseThrow(() ->
                new DatabaseException("Пользователь в базе данных не найден"));
    }

    @Override
    public User updateUser(User user) {
        Optional<User> checkUser = daoUserRepository.getUserById(user.getId());
        if (checkUser.isPresent()) {
            return daoUserRepository.updateUser(user).orElseThrow(() ->
                    new DatabaseException("Произошла ошибка при обновлении пользователя"));
        } else {
            log.info("Ошибка нет пользователя для обновления");
            throw new DatabaseException("Невозможно обновить. Такой пользователь не найден.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return daoUserRepository.getAllUsers();
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
            daoFriendsRepository.deleteUnverifiedFriend(friendId, id);
            daoFriendsRepository.addFriend(friendId, id);
            daoFriendsRepository.addFriend(id, friendId);
        } else daoFriendsRepository.addUnverifiedFriend(id, friendId);
        return getUserById(id);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        checkForPositivity(id);
        return daoFriendsRepository.getFriends(id);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        checkForPositivity(id);
        checkForPositivity(otherId);
        return daoFriendsRepository.getCommonFriends(id, otherId);
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
            daoFriendsRepository.deleteFriend(id, friendId);
            daoFriendsRepository.deleteFriend(friendId, id);
            daoFriendsRepository.addUnverifiedFriend(friendId, id);
        } else {
            daoFriendsRepository.deleteUnverifiedFriend(id, friendId);
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
