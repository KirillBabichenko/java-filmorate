package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DaoGenreRepository implements DaoGenre {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Genre> getGenreById(Long id) {
        String sql = "SELECT * FROM film_genre WHERE id_genre = ?";
        List<Genre> genre = jdbcTemplate.query(sql, (rs, rowNum) -> createGenre(rs), id);
        if (genre.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(genre.get(0));
    }

    public Collection<Genre> getAllGenres() {
        String sql = "SELECT * FROM film_genre LIMIT 100";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createGenre(rs));
    }

    private Genre createGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getInt("id_genre"),
                rs.getString("genre_name")
        );
    }
}
