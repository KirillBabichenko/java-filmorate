package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface DaoFriends {

    void addFriend(Long id, Long friendId);

    void addUnverifiedFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    void deleteUnverifiedFriend(Long id, Long friendId);

    Collection<User> getFriends(Long id);

    Collection<User> getCommonFriends(Long id, Long otherId);
}

