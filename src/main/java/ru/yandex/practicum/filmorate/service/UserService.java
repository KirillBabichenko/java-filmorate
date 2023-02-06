package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MissingFriendException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public User addFriend(Long id, Long idFriend) {
        if (inMemoryUserStorage.getUsers().get(id) == null) {
            throw new MissingFriendException("Нет пользователя с id =" + id);
        }
        if (inMemoryUserStorage.getUsers().get(idFriend) == null) {
            throw new MissingFriendException("Нет пользователя с id =" + idFriend);
        }
        if (inMemoryUserStorage.getUsers().get(idFriend).getUnverifiedFriends().contains(id)) {
            inMemoryUserStorage.getUsers().get(idFriend).getUnverifiedFriends().remove(id);
            inMemoryUserStorage.getUsers().get(id).getFriends().add(idFriend);
            inMemoryUserStorage.getUsers().get(idFriend).getFriends().add(id);
        } else {
            inMemoryUserStorage.getUsers().get(id).getUnverifiedFriends().add(idFriend);
        }
        return inMemoryUserStorage.getUsers().get(id);
    }

    public User deleteFriend(Long id, Long idFriend) {
        if (!inMemoryUserStorage.getUsers().get(id).getFriends().contains(idFriend) &&
                !inMemoryUserStorage.getUsers().get(id).getUnverifiedFriends().contains(idFriend)) {
            throw new MissingFriendException(String.format(
                    "Ошибка. У пользователя %s нет друга - %s", inMemoryUserStorage.getUsers().get(id).getName(),
                    inMemoryUserStorage.getUsers().get(idFriend).getName()));
        }
        if (inMemoryUserStorage.getUsers().get(id).getFriends().contains(idFriend)) {
            inMemoryUserStorage.getUsers().get(id).getFriends().remove(idFriend);
            inMemoryUserStorage.getUsers().get(idFriend).getFriends().remove(id);
            inMemoryUserStorage.getUsers().get(idFriend).getUnverifiedFriends().add(id);
        } else {
            inMemoryUserStorage.getUsers().get(id).getUnverifiedFriends().remove(idFriend);
        }
        return inMemoryUserStorage.getUsers().get(id);
    }

    public Collection<User> getFriends(Long idUser) {
        Collection<User> friends;
        friends = inMemoryUserStorage.getUsers().get(idUser).getFriends().stream()
                .map(idFriends -> inMemoryUserStorage.getUsers().get(idFriends))
                .collect(Collectors.toList());
        friends.addAll(inMemoryUserStorage.getUsers().get(idUser).getUnverifiedFriends().stream()
                .map(idFriends -> inMemoryUserStorage.getUsers().get(idFriends))
                .collect(Collectors.toList()));
        return friends;
    }

    public Collection<User> getCommonFriends(Long id, Long idFriend) {
        Set<Long> userFriends = new HashSet<>(inMemoryUserStorage.getUsers().get(id).getFriends());
        userFriends.addAll(inMemoryUserStorage.getUsers().get(id).getUnverifiedFriends());
        Set<Long> friendFriends = new HashSet<>(inMemoryUserStorage.getUsers().get(idFriend).getFriends());
        friendFriends.addAll(inMemoryUserStorage.getUsers().get(idFriend).getUnverifiedFriends());
        userFriends.retainAll(friendFriends);
        return userFriends.stream()
                .map(idUser -> inMemoryUserStorage.getUsers().get(idUser))
                .collect(Collectors.toList());
    }
}
