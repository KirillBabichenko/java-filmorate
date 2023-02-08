package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CreationAssistant {
    private final JdbcTemplate jdbcTemplate;

    public User createUser(ResultSet rs) throws SQLException {
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

    public Film createFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id_film");
        List<Genre> genre = getGenre(id);
        return Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("id_rating"), rs.getString("rating_name")))
                .genres(genre)
                .build();
    }

    private List<Genre> getGenre(Long idFilm) {
        String sql = "SELECT g.id_genre, fg.genre_name FROM " +
                "(genre AS g JOIN film_genre AS fg ON g.id_genre = fg.id_genre) WHERE id_film = ?" +
                "GROUP BY g.id_genre ORDER BY g.id_genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createGenre(rs), idFilm);
    }

    private Genre createGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("id_genre"), rs.getString("genre_name"));
    }
}
