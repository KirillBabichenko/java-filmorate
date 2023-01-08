package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.MissingFriendException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController userController;
    private User user;
    private Validator validator;
    private InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    public void setUp() {
        inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        userController = new UserController(inMemoryUserStorage, userService);

        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createUserNormalTest() {
        userController.createUser(user);

        assertEquals(1, inMemoryUserStorage.getUsers().size(), "Количество пользователей не совпадает");
        assertEquals(user, inMemoryUserStorage.getUsers().get(1L), "Пользователи не совпадают");
    }

    @Test
    public void createUserFailLoginTest() {
        User badUser = User.builder()
                .login("dolore ullamco")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        Assertions.assertThrows(ValidationException.class,
                () -> userController.createUser(badUser));
        assertEquals(0, inMemoryUserStorage.getUsers().size(), "Количество пользователей не совпадает");
    }

    @Test
    public void createUserEmptyLoginTest() {
        User badUser = User.builder()
                .login("")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(badUser);
        assertEquals(1, violations.size(), "Количество ошибок не совпадает");
    }

    @Test
    public void createUserEmptyNameTest() {
        User badUser = User.builder()
                .login("dolore")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        userController.createUser(badUser);

        assertEquals(1, inMemoryUserStorage.getUsers().size(), "Количество пользователей не совпадает");
        assertEquals("dolore", inMemoryUserStorage.getUsers().get(1L).getName(), "Имя пользователя не совпадает");
    }

    @Test
    public void createUserFailEmailTest() {
        User badUser = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mailmail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(badUser);
        assertEquals(1, violations.size(), "Количество ошибок не совпадает");
    }

    @Test
    public void createUserFailBirthdayTest() {
        User badUser = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2946, 8, 20))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(badUser);
        assertEquals(1, violations.size(), "Количество ошибок не совпадает");
    }

    @Test
    public void updateUserNormalTest() {
        User secondUser = User.builder()
                .id(1L)
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        userController.createUser(user);
        userController.updateUser(secondUser);

        assertEquals(1, inMemoryUserStorage.getUsers().size(), "Количество пользователей не совпадает");
        assertEquals(secondUser, inMemoryUserStorage.getUsers().get(1L), "Пользователи не совпадают");
    }

    @Test
    public void updateUnknownUserTest() {
        User secondUser = User.builder()
                .id(9999L)
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        userController.createUser(user);

        Assertions.assertThrows(MissingFriendException.class,
                () -> userController.updateUser(secondUser));
        assertEquals(1, inMemoryUserStorage.getUsers().size(), "Количество пользователей не совпадает");
        assertEquals(user, inMemoryUserStorage.getUsers().get(1L), "Пользователи не совпадают");
    }

    @Test
    public void getAllUserTest() {
        User secondUser = User.builder()
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        userController.createUser(user);
        userController.createUser(secondUser);

        Collection<User> allUsers = userController.getAllUsers();

        assertEquals(2, allUsers.size(), "Количество пользователей не совпадает");
        assertTrue(allUsers.contains(user), "Пользователь не найден");
    }

    @Test
    public void getUserByIdNormalTest() {
        userController.createUser(user);
        User testUser = userController.getUserById(1L);

        assertEquals(user, testUser, "Пользователи не совпадают");
    }

    @Test
    public void getUserByIdIncorrectIdTest() {
        userController.createUser(user);

        Assertions.assertThrows(MissingFriendException.class,
                () -> userController.getUserById(9999L), "Должна быть ошибка MissingFriendException");
    }

    @Test
    public void addFriendNormalTest() {
        User secondUser = User.builder()
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .build();
        userController.createUser(user);
        userController.createUser(secondUser);

        userController.addFriend(1L, 2L);

        assertTrue(inMemoryUserStorage.getUsers().get(1L).getFriends().contains(2L),
                "В списке друзей пользователя c id = 1L должен быть пользователь с id = 2L");
        assertTrue(inMemoryUserStorage.getUsers().get(2L).getFriends().contains(1L),
                "В списке друзей пользователя c id = 2L должен быть пользователь с id = 1L");
        assertEquals(1, inMemoryUserStorage.getUsers().get(2L).getFriends().size(),
                "В списке друзей должен быть один пользователь");
    }

    @Test
    public void addFriendNegativeIdTest() {
        userController.createUser(user);

        Assertions.assertThrows(IncorrectParameterException.class,
                () -> userController.addFriend(1L, -9999L), "Должна быть ошибка IncorrectParameterException");
    }

    @Test
    public void addFriendIncorrectIdTest() {
        userController.createUser(user);

        Assertions.assertThrows(MissingFriendException.class,
                () -> userController.addFriend(1L, 2L), "Должна быть ошибка MissingFriendException");
    }

    @Test
    public void deleteFriendNormalTest() {
        User secondUser = User.builder()
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .build();
        userController.createUser(user);
        userController.createUser(secondUser);

        userController.addFriend(1L, 2L);
        assertEquals(1, inMemoryUserStorage.getUsers().get(2L).getFriends().size(),
                "В списке друзей должен быть один пользователь");

        userController.deleteFriend(1L, 2L);
        assertEquals(0, inMemoryUserStorage.getUsers().get(1L).getFriends().size(),
                "Список друзей пользователя id =1L должен быть пуст");
        assertEquals(0, inMemoryUserStorage.getUsers().get(2L).getFriends().size(),
                "Список друзей пользователя id =2L должен быть пуст");
    }

    @Test
    public void getFriendsNormalTest() {
        User secondUser = User.builder()
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .build();
        userController.createUser(user);
        userController.createUser(secondUser);
        userController.addFriend(1L, 2L);
        Collection<User> listFriends = userController.getFriends(1L);

        assertEquals(1, listFriends.size(), "В списке друзей должен быть один пользователь");
        assertTrue(listFriends.contains(secondUser), "Ошибка. Пользователи не совпадают");
    }

    @Test
    public void getFriendsEmptyListTest() {
        userController.createUser(user);
        Collection<User> listFriends = userController.getFriends(1L);

        assertEquals(0, listFriends.size(), "Список должен быть пустой");
    }

    @Test
    public void getCommonFriendsNormalTest() {
        User secondUser = User.builder()
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .build();
        User thirdUser = User.builder()
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .build();
        userController.createUser(user);
        userController.createUser(secondUser);
        userController.createUser(thirdUser);

        userController.addFriend(1L, 2L);
        userController.addFriend(1L, 3L);
        userController.addFriend(2L, 3L);

        Collection<User> commonFriends = userController.getCommonFriends(1L, 2L);

        assertEquals(1, commonFriends.size(),
                "В списке общих друзей должен быть один пользователь");
        assertTrue(commonFriends.contains(thirdUser), "Ошибка. Пользователи не совпадают");
    }
}
