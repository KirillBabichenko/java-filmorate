package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.DaoUserService;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDaoTest {
    private final DaoUserService daoUserService;
    private User user;
    private User secondUser;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .build();
        secondUser = User.builder()
                .login("doloreUpdate")
                .name("est adipisicing")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .unverifiedFriends((new HashSet<>()))
                .build();
    }

    @Test
    public void saveUserNormalTest() {
        Optional<User> userOptional = daoUserService.saveUser(user);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                )
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "dolore")
                );
    }

    @Test
    public void findUserByIdNormalTest() {
        daoUserService.saveUser(user);
        Optional<User> userOptional = daoUserService.getUserById(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                )
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "dolore")
                );
    }

    @Test
    public void findUserByMissingIdTest() {
        daoUserService.saveUser(user);
        Optional<User> userOptional = daoUserService.getUserById(999L);
        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    public void updateUserNormalTest() {
        User updateUser = User.builder()
                .id(1L)
                .login("doloreUpdate")
                .name("est adipisicing")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .build();
        daoUserService.saveUser(user);
        Optional<User> userOptional = daoUserService.updateUser(updateUser);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                )
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "doloreUpdate")
                );
    }

    @Test
    public void getAllUsersTest() {
        daoUserService.saveUser(user);
        daoUserService.saveUser(secondUser);
        Collection<User> users2 = daoUserService.getAllUsers();
        secondUser.setId(2L);
        assertThat(users2)
                .isNotEmpty()
                .hasSize(2)
                .contains(secondUser);
    }

    @Test
    public void getEmptyFriendTest() {
        daoUserService.saveUser(user);
        Collection<User> emptyFriends = daoUserService.getFriends(1L);
        assertThat(emptyFriends)
                .isEmpty();
    }

    @Test
    public void addAndGetFriendNormalTest() {
        daoUserService.saveUser(user);
        daoUserService.saveUser(secondUser);
        daoUserService.addFriend(1L, 2L);
        user.setId(1L);
        secondUser.setId(2L);

        Collection<User> friendsFirst = daoUserService.getFriends(1L);
        assertThat(friendsFirst)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
        Collection<User> friendsSecond = daoUserService.getFriends(2L);
        assertThat(friendsSecond)
                .isEmpty();
    }


    @Test
    public void addUnverifiedFriendNormalTest() {
        daoUserService.saveUser(user);
        daoUserService.saveUser(secondUser);
        daoUserService.addUnverifiedFriend(1L, 2L);
        user.setId(1L);
        secondUser.setId(2L);

        Collection<User> friendsFirst = daoUserService.getFriends(1L);
        assertThat(friendsFirst)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
        Collection<User> friendsSecond = daoUserService.getFriends(2L);
        assertThat(friendsSecond)
                .isEmpty();
    }

    @Test
    public void deleteFriendNormalTest() {
        daoUserService.saveUser(user);
        daoUserService.saveUser(secondUser);
        daoUserService.addFriend(1L, 2L);
        user.setId(1L);
        secondUser.setId(2L);

        Collection<User> friendsFirst = daoUserService.getFriends(1L);
        assertThat(friendsFirst)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
        daoUserService.deleteFriend(1L, 2L);
    }

    @Test
    public void deleteUnverifiedFriendNormalTest() {
        daoUserService.saveUser(user);
        daoUserService.saveUser(secondUser);
        daoUserService.addUnverifiedFriend(1L, 2L);
        user.setId(1L);
        secondUser.setId(2L);

        Collection<User> friendsFirst = daoUserService.getFriends(1L);
        assertThat(friendsFirst)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
        daoUserService.deleteUnverifiedFriend(1L, 2L);
    }

    @Test
    public void getCommonFriendsNormalTest() {
        User thirdUser = User.builder()
                .login("thirdUser")
                .name("thirdUser")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1948, 8, 20))
                .friends(new HashSet<>())
                .unverifiedFriends((new HashSet<>()))
                .build();
        daoUserService.saveUser(user);
        daoUserService.saveUser(secondUser);
        daoUserService.saveUser(thirdUser);
        user.setId(1L);
        secondUser.setId(2L);
        thirdUser.setId(3L);

        Collection<User> emptyFriends = daoUserService.getCommonFriends(1L, 2L);
        assertThat(emptyFriends)
                .isEmpty();

        daoUserService.addUnverifiedFriend(1L, 2L);
        Collection<User> emptyFriends2 = daoUserService.getCommonFriends(1L, 2L);
        assertThat(emptyFriends2)
                .isEmpty();

        daoUserService.addUnverifiedFriend(3L, 2L);

        Collection<User> commonFriends = daoUserService.getCommonFriends(1L, 3L);
        assertThat(commonFriends)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
    }
}
