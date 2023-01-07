package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;

@Getter
@Setter
@RestController
@RequiredArgsConstructor
public class UserController {
    final private InMemoryUserStorage inMemoryUserStorage;
    final private UserService userService;

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        userService.checkForPositivity(id);
        return inMemoryUserStorage.getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.checkForPositivity(id);
        userService.checkForPositivity(friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.checkForPositivity(id);
        userService.checkForPositivity(friendId);
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        userService.checkForPositivity(id);
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        userService.checkForPositivity(id);
        userService.checkForPositivity(otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
