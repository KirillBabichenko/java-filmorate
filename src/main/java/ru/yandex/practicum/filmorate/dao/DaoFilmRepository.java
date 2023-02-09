package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Primary
@Slf4j
@Repository
@RequiredArgsConstructor
public class DaoFilmRepository implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final CreationAssistant creationAssistant;

    @Override
    public Optional<Film> saveFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Film")
                .usingGeneratedKeyColumns("id_film");
        Long idFilm = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).longValue();
        if (film.getGenres() != null) {
            saveGenre(film, idFilm);
        }
        log.info("DaoFilmService фильм сохранен");
        return getFilmById(idFilm);
    }

    public Optional<Film> getFilmById(Long id) {
        String sql = "SELECT * FROM (Film AS f LEFT JOIN rating AS r ON f.id_rating = r.id_rating) WHERE id_film = ?";
        List<Film> film = jdbcTemplate.query(sql, (rs, rowNum) -> creationAssistant.createFilm(rs), id);
        if (film.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(film.get(0));
    }

    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM (Film AS f LEFT JOIN rating AS r ON f.id_rating = r.id_rating)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> creationAssistant.createFilm(rs));
    }

    public Optional<Film> updateFilm(Film film) {
        Optional<Film> checkFilm = getFilmById(film.getId());
        if (checkFilm.isPresent()) {
            String sql = "UPDATE Film SET name = ?, description = ?, release_date = ?, duration = ?, id_rating = ? " +
                    "WHERE id_film = ?;";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getMpa().getId(), film.getId());
            if (film.getGenres() != null) {
                if (!checkFilm.get().getGenres().isEmpty()) {
                    deleteGenre(film);
                }
                saveGenre(film, film.getId());
            }
            return getFilmById(film.getId());
        } else {
            log.info("Ошибка нет фильма для обновления");
            throw new DatabaseException("Невозможно обновить. Такой фильм не найден.");
        }
    }

    private void deleteGenre(Film film) {
        String sqlQuery = "DELETE FROM genre WHERE id_film = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void saveGenre(Film film, Long id) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genre")
                .usingGeneratedKeyColumns("id_line_genre");
        for (Genre genre : film.getGenres()) {
            simpleJdbcInsert.executeAndReturnKey(genreToMap(genre, id)).longValue();
        }
    }

    private Map<String, Object> genreToMap(Genre genre, Long id) {
        Map<String, Object> genreMap = new HashMap<>();
        genreMap.put("id_film", id);
        genreMap.put("id_genre", genre.getId());
        return genreMap;
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> filmMap = new HashMap<>();
        filmMap.put("name", film.getName());
        filmMap.put("description", film.getDescription());
        filmMap.put("release_date", film.getReleaseDate());
        filmMap.put("duration", film.getDuration());
        filmMap.put("id_rating", film.getMpa().getId());
        return filmMap;
    }
}
