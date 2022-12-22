package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController userController;
    private User user;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createUserNormalTest() {
        userController.createUser(user);

        assertEquals(1, userController.getUsers().size(), "Количество пользователей не совпадает");
        assertEquals(user, userController.getUsers().get(1), "Пользователи не совпадают");
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
        assertEquals(0, userController.getUsers().size(), "Количество пользователей не совпадает");
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

        assertEquals(1, userController.getUsers().size(), "Количество пользователей не совпадает");
        assertEquals("dolore", userController.getUsers().get(1).getName(), "Имя пользователя не совпадает");
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
                .id(1)
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        userController.createUser(user);
        userController.updateUser(secondUser);

        assertEquals(1, userController.getUsers().size(), "Количество пользователей не совпадает");
        assertEquals(secondUser, userController.getUsers().get(1), "Пользователи не совпадают");
    }

    @Test
    public void updateUnknownUserTest() {
        User secondUser = User.builder()
                .id(9999)
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        userController.createUser(user);

        Assertions.assertThrows(ValidationException.class,
                () -> userController.updateUser(secondUser));
        assertEquals(1, userController.getUsers().size(), "Количество пользователей не совпадает");
        assertEquals(user, userController.getUsers().get(1), "Пользователи не совпадают");
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
}
