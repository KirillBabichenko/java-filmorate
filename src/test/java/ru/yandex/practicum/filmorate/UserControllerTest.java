package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {
    private final UserController userController;
    private User user;
    private User secondUser;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .unverifiedFriends(new HashSet<>())
                .build();
        secondUser = User.builder()
                .id(1L)
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .unverifiedFriends(new HashSet<>())
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createUserNormalTest() {
        userController.createUser(user);
        user.setId(1L);

        assertEquals(1, userController.getAllUsers().size(), "Количество пользователей не совпадает");
        assertEquals(user, userController.getUserById(1L), "Пользователи не совпадают");
    }

    @Test
    public void createUserFailLoginTest() {
        User badUser = User.builder()
                .login("dolore ullamco")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        badUser.setId(1L);

        Assertions.assertThrows(ValidationException.class,
                () -> userController.createUser(badUser));
        assertEquals(0, userController.getAllUsers().size(), "Количество пользователей не совпадает");
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

        assertEquals(1, userController.getAllUsers().size(), "Количество пользователей не совпадает");
        assertEquals("dolore", userController.getUserById(1L).getName(), "Имя пользователя не совпадает");
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
        secondUser.setId(1L);
        userController.createUser(user);
        userController.updateUser(secondUser);

        assertEquals(1, userController.getAllUsers().size(), "Количество пользователей не совпадает");
        assertEquals(secondUser, userController.getUserById(1L), "Пользователи не совпадают");
    }

    @Test
    public void updateUnknownUserTest() {
        secondUser.setId(9999L);
        userController.createUser(user);
        user.setId(1L);

        Assertions.assertThrows(DatabaseException.class,
                () -> userController.updateUser(secondUser), "Должна быть ошибка DatabaseException");
        assertEquals(1, userController.getAllUsers().size(), "Количество пользователей не совпадает");
        assertEquals(user, userController.getUserById(1L), "Пользователи не совпадают");
    }

    @Test
    public void getAllUserTest() {
        userController.createUser(user);
        userController.createUser(secondUser);
        user.setId(1L);
        Collection<User> allUsers = userController.getAllUsers();

        assertEquals(2, allUsers.size(), "Количество пользователей не совпадает");
        assertTrue(allUsers.contains(user), "Пользователь не найден");
    }

    @Test
    public void getUserByIdNormalTest() {
        userController.createUser(user);
        user.setId(1L);
        User testUser = userController.getUserById(1L);

        assertEquals(user, testUser, "Пользователи не совпадают");
    }

    @Test
    public void getUserByIdIncorrectIdTest() {
        userController.createUser(user);

        Assertions.assertThrows(DatabaseException.class,
                () -> userController.getUserById(9999L), "Должна быть ошибка DatabaseException");
    }

    @Test
    public void addFriendNormalTest() {
        userController.createUser(user);
        userController.createUser(secondUser);
        user.setId(1L);
        secondUser.setId(2L);
        userController.addFriend(1L, 2L);

        assertTrue(userController.getFriends(1L).contains(secondUser),
                "В списке друзей пользователя c id = 1L должен быть пользователь с id = 2L");
        assertFalse(userController.getFriends(2L).contains(user),
                "В списке друзей пользователя c id = 2L не должно быть пользователя с id = 1L");
        assertEquals(0, userController.getFriends(2L).size(),
                "Список друзей пользователя id = 2L должен быть пуст");
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

        Assertions.assertThrows(DatabaseException.class,
                () -> userController.addFriend(1L, 2L), "Должна быть ошибка DatabaseException");
    }

    @Test
    public void deleteFriendNormalTest() {
        userController.createUser(user);
        userController.createUser(secondUser);

        userController.addFriend(1L, 2L);
        assertEquals(1, userController.getFriends(1L).size(),
                "В списке друзей должен быть один пользователь");

        userController.deleteFriend(1L, 2L);
        assertEquals(0, userController.getFriends(1L).size(),
                "Список друзей пользователя id =1L должен быть пуст");
        assertEquals(0, userController.getFriends(2L).size(),
                "Список друзей пользователя id =2L должен быть пуст");
    }

    @Test
    public void getFriendsNormalTest() {
        userController.createUser(user);
        userController.createUser(secondUser);
        secondUser.setId(2L);
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
        User thirdUser = User.builder()
                .login("Ivan")
                .name("Grozny")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .unverifiedFriends(new HashSet<>())
                .build();
        userController.createUser(user);
        userController.createUser(secondUser);
        userController.createUser(thirdUser);
        thirdUser.setId(3L);

        userController.addFriend(1L, 3L);
        userController.addFriend(2L, 3L);
        Collection<User> commonFriends = userController.getCommonFriends(1L, 2L);

        assertEquals(1, commonFriends.size(),
                "В списке общих друзей должен быть один пользователь");
        assertTrue(commonFriends.contains(thirdUser), "Ошибка. Пользователи не совпадают");
    }
}