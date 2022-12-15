package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Getter
@Setter
@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final LocalDate startDate = LocalDate.of(1895, Month.DECEMBER, 28);
    private int idFilm = 1;
    private Map<Integer, Film> films = new HashMap<>();

    private Film validateFilm(Film film) {
        if (film.getName().isEmpty()) {
            log.info("Ошибка с названием - {}", film);
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.info("Ошибка с длиной описания - {}", film);
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(startDate)) {
            log.info("Ошибка с датой релиза - {}", film);
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.info("Ошибка с длительностью - {}", film);
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        return film;
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film) {
        Film validateFilm = validateFilm(film);
        validateFilm.setId(idFilm);
        films.put(idFilm++, validateFilm);
        return validateFilm;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            Film validateFilm = validateFilm(film);
            films.put(validateFilm.getId(), validateFilm);
            return validateFilm;
        } else {
            throw new ValidationException("Фильма с таким id нет.");
        }
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return films.values();
    }
}
