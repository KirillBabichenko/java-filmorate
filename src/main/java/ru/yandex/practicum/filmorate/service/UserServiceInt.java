package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserServiceInt {

    User saveUser(User user);

    User getUserById(Long id);

    User updateUser(User user);

    Collection<User> getAllUsers();

    User addFriend(Long id, Long friendId);

    Collection<User> getFriends(Long id);

    Collection<User> getCommonFriends(Long id, Long otherId);

    User deleteFriend(Long id, Long friendId);
}
