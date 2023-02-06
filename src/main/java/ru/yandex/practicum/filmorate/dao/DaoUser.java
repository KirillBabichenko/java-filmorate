package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface DaoUser {
    Optional<User> saveUser(User user);

    Optional<User> getUserById(Long id);

    Optional<User> updateUser(User user);

    Collection<User> getAllUsers();

    void addFriend(Long id, Long friendId);

    void addUnverifiedFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    void deleteUnverifiedFriend(Long id, Long friendId);

    Collection<User> getFriends(Long id);

    Collection<User> getCommonFriends(Long id, Long otherId);
}
