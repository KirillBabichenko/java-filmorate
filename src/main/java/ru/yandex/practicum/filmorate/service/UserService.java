package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
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
        inMemoryUserStorage.getUsers().get(id).getFriends().add(idFriend);
        inMemoryUserStorage.getUsers().get(idFriend).getFriends().add(id);
        return inMemoryUserStorage.getUsers().get(id);
    }

    public User deleteFriend(Long id, Long idFriend) {
        if (!inMemoryUserStorage.getUsers().get(id).getFriends().contains(idFriend)) {
            throw new MissingFriendException(String.format(
                    "Ошибка. У пользователя %s нет друга - %s", inMemoryUserStorage.getUsers().get(id).getName(),
                    inMemoryUserStorage.getUsers().get(idFriend).getName()));
        }
        inMemoryUserStorage.getUsers().get(id).getFriends().remove(idFriend);
        inMemoryUserStorage.getUsers().get(idFriend).getFriends().remove(id);
        return inMemoryUserStorage.getUsers().get(id);
    }

    public Collection<User> getFriends(Long idUser) {
        return inMemoryUserStorage.getUsers().get(idUser).getFriends().stream()
                .map(idFriends -> inMemoryUserStorage.getUsers().get(idFriends))
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long id, Long idFriend) {
        Set<Long> userFriends = new HashSet<>(inMemoryUserStorage.getUsers().get(id).getFriends());
        Set<Long> friendFriends = new HashSet<>(inMemoryUserStorage.getUsers().get(idFriend).getFriends());
        userFriends.retainAll(friendFriends);
        return userFriends.stream()
                .map(idUser -> inMemoryUserStorage.getUsers().get(idUser))
                .collect(Collectors.toList());
    }

    public void checkForPositivity(Long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("Некорректно указан параметр " + id + ". Должен быть больше нуля.");
        }
    }
}
