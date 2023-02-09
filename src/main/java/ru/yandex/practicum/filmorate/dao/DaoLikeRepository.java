package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DaoLikeRepository implements DaoLike {
    private final JdbcTemplate jdbcTemplate;
    private final CreationAssistant creationAssistant;

    @Override
    public boolean addLike(Long idFilm, Long idUser) {
        String sql = "INSERT INTO Likes (id_film, id_user) values (?, ?);";
        return jdbcTemplate.update(sql, idFilm, idUser) > 0;
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM Likes where id_film = ? AND id_user = ?";
        return jdbcTemplate.update(sqlQuery, id, userId) > 0;
    }

    @Override
    public List<Film> getPopularFilms(Integer amount) {
        String sql = "SELECT f.ID_FILM, f.NAME, f.DESCRIPTION, f.RELEASE_DATE , f.DURATION, f.ID_RATING, r.rating_name " +
                "FROM Film AS f LEFT JOIN Likes AS L ON f.ID_FILM = l.ID_FILM " +
                "LEFT JOIN rating AS r ON f.id_rating = r.id_rating " +
                "GROUP BY f.id_film ORDER BY COUNT (l.id_user) DESC LIMIT ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> creationAssistant.createFilm(rs), amount);
    }
}
