package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.DaoFriendsRepository;
import ru.yandex.practicum.filmorate.dao.DaoUserRepository;
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
    private final DaoUserRepository daoUserRepository;
    private final DaoFriendsRepository daoFriendsRepository;
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
        Optional<User> userOptional = daoUserRepository.saveUser(user);
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
        daoUserRepository.saveUser(user);
        Optional<User> userOptional = daoUserRepository.getUserById(1L);
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
        daoUserRepository.saveUser(user);
        Optional<User> userOptional = daoUserRepository.getUserById(999L);
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
        daoUserRepository.saveUser(user);
        Optional<User> userOptional = daoUserRepository.updateUser(updateUser);
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
        daoUserRepository.saveUser(user);
        daoUserRepository.saveUser(secondUser);
        Collection<User> users2 = daoUserRepository.getAllUsers();
        secondUser.setId(2L);
        assertThat(users2)
                .isNotEmpty()
                .hasSize(2)
                .contains(secondUser);
    }

    @Test
    public void getEmptyFriendTest() {
        daoUserRepository.saveUser(user);
        Collection<User> emptyFriends = daoFriendsRepository.getFriends(1L);
        assertThat(emptyFriends)
                .isEmpty();
    }

    @Test
    public void addAndGetFriendNormalTest() {
        daoUserRepository.saveUser(user);
        daoUserRepository.saveUser(secondUser);
        daoFriendsRepository.addFriend(1L, 2L);
        user.setId(1L);
        secondUser.setId(2L);

        Collection<User> friendsFirst = daoFriendsRepository.getFriends(1L);
        assertThat(friendsFirst)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
        Collection<User> friendsSecond = daoFriendsRepository.getFriends(2L);
        assertThat(friendsSecond)
                .isEmpty();
    }


    @Test
    public void addUnverifiedFriendNormalTest() {
        daoUserRepository.saveUser(user);
        daoUserRepository.saveUser(secondUser);
        daoFriendsRepository.addUnverifiedFriend(1L, 2L);
        user.setId(1L);
        secondUser.setId(2L);

        Collection<User> friendsFirst = daoFriendsRepository.getFriends(1L);
        assertThat(friendsFirst)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
        Collection<User> friendsSecond = daoFriendsRepository.getFriends(2L);
        assertThat(friendsSecond)
                .isEmpty();
    }

    @Test
    public void deleteFriendNormalTest() {
        daoUserRepository.saveUser(user);
        daoUserRepository.saveUser(secondUser);
        daoFriendsRepository.addFriend(1L, 2L);
        user.setId(1L);
        secondUser.setId(2L);

        Collection<User> friendsFirst = daoFriendsRepository.getFriends(1L);
        assertThat(friendsFirst)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
        daoFriendsRepository.deleteFriend(1L, 2L);
    }

    @Test
    public void deleteUnverifiedFriendNormalTest() {
        daoUserRepository.saveUser(user);
        daoUserRepository.saveUser(secondUser);
        daoFriendsRepository.addUnverifiedFriend(1L, 2L);
        user.setId(1L);
        secondUser.setId(2L);

        Collection<User> friendsFirst = daoFriendsRepository.getFriends(1L);
        assertThat(friendsFirst)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
        daoFriendsRepository.deleteUnverifiedFriend(1L, 2L);
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
        daoUserRepository.saveUser(user);
        daoUserRepository.saveUser(secondUser);
        daoUserRepository.saveUser(thirdUser);
        user.setId(1L);
        secondUser.setId(2L);
        thirdUser.setId(3L);

        Collection<User> emptyFriends = daoFriendsRepository.getCommonFriends(1L, 2L);
        assertThat(emptyFriends)
                .isEmpty();

        daoFriendsRepository.addUnverifiedFriend(1L, 2L);
        Collection<User> emptyFriends2 = daoFriendsRepository.getCommonFriends(1L, 2L);
        assertThat(emptyFriends2)
                .isEmpty();

        daoFriendsRepository.addUnverifiedFriend(3L, 2L);

        Collection<User> commonFriends = daoFriendsRepository.getCommonFriends(1L, 3L);
        assertThat(commonFriends)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondUser);
    }
}
