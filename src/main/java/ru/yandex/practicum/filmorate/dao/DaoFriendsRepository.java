package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class DaoFriendsRepository implements DaoFriends {
    private final JdbcTemplate jdbcTemplate;
    private final DaoUserRepository daoUserRepository;

    @Override
    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO friends (id_user, id_friends) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void addUnverifiedFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO unverified_friends (id_user, id_friends) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM friends WHERE id_user = ? AND id_friends = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void deleteUnverifiedFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM unverified_friends WHERE id_user = ? AND id_friends = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        String sql = "SELECT * FROM Users WHERE id_user IN " +
                "(SELECT id_friends FROM friends WHERE id_user = ?)" +
                "OR id_user IN (SELECT id_friends FROM unverified_friends WHERE id_user = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> daoUserRepository.createUser(rs), id, id);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        String sql = "(SELECT u.ID_USER, u.LOGIN, u.NAME, u.EMAIL, u.BIRTHDAY_DATE FROM users AS u " +
                "JOIN friends AS f ON u.id_user = f.id_friends WHERE f.id_user = ? " +
                "UNION SELECT u.ID_USER, u.LOGIN, u.NAME, u.EMAIL, u.BIRTHDAY_DATE FROM users AS u " +
                "JOIN unverified_friends AS uf ON u.id_user = uf.id_friends WHERE uf.id_user = ?) " +
                "INTERSECT (SELECT u.ID_USER, u.LOGIN, u.NAME, u.EMAIL, u.BIRTHDAY_DATE FROM users AS u " +
                "JOIN friends AS f ON u.id_user = f.id_friends WHERE f.id_user = ? " +
                "UNION SELECT u.ID_USER, u.LOGIN, u.NAME, u.EMAIL, u.BIRTHDAY_DATE FROM users AS u " +
                "JOIN unverified_friends AS uf ON u.id_user = uf.id_friends WHERE uf.id_user = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> daoUserRepository.createUser(rs), id, id, otherId, otherId);
    }
}
