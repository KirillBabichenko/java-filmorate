package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class DaoUserRepository implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final CreationAssistant creationAssistant;

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
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> creationAssistant.createUser(rs), id);
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
        return jdbcTemplate.query(sql, (rs, rowNum) -> creationAssistant.createUser(rs));
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("login", user.getLogin());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("birthday_date", user.getBirthday());
        return userMap;
    }
}
