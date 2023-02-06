package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DaoUserService implements DaoUser {
    private final JdbcTemplate jdbcTemplate;

    public Optional<User> saveUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Users")
                .usingGeneratedKeyColumns("id_user");
        Long idUser = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).longValue();
        return getUserById(idUser);
    }

    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM Users WHERE id_user = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs), id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(0));
    }


    public Optional<User> updateUser(User user) {
        String sql = "UPDATE Users SET login = ?, name = ?, email = ?, birthday_date = ?";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        return getUserById(user.getId());
    }

    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM Users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs));
    }

    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO friends (id_user, id_friends) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    public void addUnverifiedFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO unverified_friends (id_user, id_friends) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE id_user = ? AND id_friends = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    public void deleteUnverifiedFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM unverified_friends WHERE id_user = ? AND id_friends = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    public Collection<User> getFriends(Long id) {
        String sql = "SELECT * FROM Users WHERE id_user IN " +
                "(SELECT id_friends FROM friends WHERE id_user = ?)" +
                "OR id_user IN (SELECT id_friends FROM unverified_friends WHERE id_user = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs), id, id);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        String sql = "SELECT * FROM Users WHERE id_user IN (SELECT id_friends FROM " +
                "((SELECT id_friends FROM friends WHERE id_user = ?) " +
                "UNION (SELECT id_friends FROM unverified_friends WHERE id_user = ?)) AS friends_1 " +
                "INTERSECT SELECT id_friends FROM ((SELECT id_friends FROM friends WHERE id_user = ?) " +
                "UNION (SELECT id_friends FROM unverified_friends WHERE id_user = ?)) AS friends_2);";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs), id, id, otherId, otherId);
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("login", user.getLogin());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("birthday_date", user.getBirthday());
        return userMap;
    }

    private User createUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id_user");
        return User.builder()
                .id(id)
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday_date").toLocalDate())
                .friends(getIdFriends(id))
                .unverifiedFriends(getIdUnverifiedFriends(id))
                .build();
    }

    private Set<Long> getIdFriends(Long id) {
        String sql = "SELECT id_friends FROM friends WHERE id_user = ?";
        List<Long> idFriends = jdbcTemplate.query(sql, (rs, rowNum) -> createListFriend(rs), id);
        return new HashSet<>(idFriends);
    }

    private Set<Long> getIdUnverifiedFriends(Long id) {
        String sql = "SELECT id_friends FROM unverified_friends WHERE id_user = ?";
        List<Long> idFriends = jdbcTemplate.query(sql, (rs, rowNum) -> createListFriend(rs), id);
        return new HashSet<>(idFriends);
    }

    private Long createListFriend(ResultSet rs) throws SQLException {
        return rs.getLong("id_friends");
    }
}
