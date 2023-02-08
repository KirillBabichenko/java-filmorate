package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.MissingFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final LocalDate startDate = LocalDate.of(1895, Month.DECEMBER, 28);
    private Long idFilm = 1L;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> saveFilm(Film film) {
        Film validateFilm = validateFilm(film);
        validateFilm.setId(idFilm);
        films.put(idFilm++, validateFilm);
        return Optional.of(validateFilm);
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            Film validateFilm = validateFilm(film);
            films.put(validateFilm.getId(), validateFilm);
            return Optional.of(validateFilm);
        } else {
            throw new MissingFilmException("Фильма с таким id нет.");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        if (films.get(id) == null) {
            throw new MissingFilmException(String.format(
                    "Ошибка. Фильма с id - %s не найдено.", id));
        }
        return Optional.of(films.get(id));
    }

    private Film validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(startDate)) {
            log.info("Ошибка с датой релиза - {}", film);
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года");
        }
        return film;
    }
}
