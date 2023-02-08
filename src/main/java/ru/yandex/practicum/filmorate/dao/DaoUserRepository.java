package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DaoUserRepository implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> saveUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Users")
                .usingGeneratedKeyColumns("id_user");
        Long idUser = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).longValue();
        return getUserById(idUser);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM Users WHERE id_user = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs), id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(0));
    }

    @Override
    public Optional<User> updateUser(User user) {
        String sql = "UPDATE Users SET login = ?, name = ?, email = ?, birthday_date = ? WHERE id_user = ?";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        return getUserById(user.getId());
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM Users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createUser(rs));
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("login", user.getLogin());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("birthday_date", user.getBirthday());
        return userMap;
    }

    User createUser(ResultSet rs) throws SQLException {
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
