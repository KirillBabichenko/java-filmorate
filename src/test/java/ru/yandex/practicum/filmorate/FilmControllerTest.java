package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTest {
    private FilmController filmController;
    private Film film;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
        film = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1967, 3, 25), 100);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createFilmNormalTest() {
        filmController.createFilm(film);

        assertEquals(1, filmController.getFilms().size(), "Количество фильмов не совпадает");
        assertEquals(film, filmController.getFilms().get(1), "Фильмы не совпадают");
    }

    @Test
    public void createFilmFailNameTest() {
        Film badFilm = new Film("", "adipisicing", LocalDate.of(1967, 3, 25), 100);

        Set<ConstraintViolation<Film>> violations = validator.validate(badFilm);
        assertEquals(1, violations.size(), "Количество ошибок не совпадает");
    }

    @Test
    public void createFilmFailDescriptionTest() {
        Film badFilm = new Film("Film", "Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, " +
                "который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», " +
                "стал кандидатом Коломбани.", LocalDate.of(1967, 3, 25), 100);

        Set<ConstraintViolation<Film>> violations = validator.validate(badFilm);
        assertEquals(1, violations.size(), "Количество ошибок не совпадает");
    }

    @Test
    public void createFilmFailReleaseDateTest() {
        Film badFilm = new Film("Film", "Description", LocalDate.of(1825, 3, 25), 100);

        Assertions.assertThrows(ValidationException.class,
                () -> filmController.createFilm(badFilm));
        assertEquals(0, filmController.getFilms().size(), "Количество фильмов не совпадает");
    }

    @Test
    public void createFilmFailDurationTest() {
        Film badFilm = new Film("Film", "Description", LocalDate.of(1925, 3, 25), -100);

        Set<ConstraintViolation<Film>> violations = validator.validate(badFilm);
        assertEquals(1, violations.size(), "Количество ошибок не совпадает");
    }

    @Test
    public void updateFilmNormalTest() {
        Film updateFilm = new Film("Film Updated", "New film update description", LocalDate.of(1967, 3, 25), 100);
        updateFilm.setId(1);
        filmController.createFilm(film);
        filmController.updateFilm(updateFilm);

        assertEquals(1, filmController.getFilms().size(), "Количество фильмов не совпадает");
        assertEquals(updateFilm, filmController.getFilms().get(1), "Фильмы не совпадают");
    }

    @Test
    public void updateUnknownFilmFailTest() {
        Film updateFilm = new Film("Film Updated", "New film update description", LocalDate.of(1967, 3, 25), 100);
        updateFilm.setId(9999);
        filmController.createFilm(film);

        Assertions.assertThrows(ValidationException.class,
                () -> filmController.updateFilm(updateFilm));
        assertEquals(1, filmController.getFilms().size(), "Количество фильмов не совпадает");
        assertEquals(film, filmController.getFilms().get(1), "Фильмы не совпадают");
    }

    @Test
    public void getAllFilmsTest() {
        Film newFilm = new Film("Film Updated", "New film update description", LocalDate.of(1967, 3, 25), 100);
        filmController.createFilm(film);
        filmController.createFilm(newFilm);

        Collection<Film> allFilms = filmController.getAllFilms();

        assertEquals(2, allFilms.size(), "Количество фильмов не совпадает");
        assertTrue(allFilms.contains(film), "Фильм не найден");
    }
}
