package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.MissingFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private FilmController filmController;
    private Film film;
    private Validator validator;
    private InMemoryFilmStorage inMemoryFilmStorage;

    @BeforeEach
    public void setUp() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(inMemoryFilmStorage);
        filmController = new FilmController(inMemoryFilmStorage, filmService);
        film = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1967, 3, 25), 100);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createFilmNormalTest() {
        filmController.createFilm(film);

        assertEquals(1, inMemoryFilmStorage.getFilms().size(), "Количество фильмов не совпадает");
        assertEquals(film, inMemoryFilmStorage.getFilms().get(1L), "Фильмы не совпадают");
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
        assertEquals(0, inMemoryFilmStorage.getFilms().size(), "Количество фильмов не совпадает");
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
        updateFilm.setId(1L);
        filmController.createFilm(film);
        filmController.updateFilm(updateFilm);

        assertEquals(1, inMemoryFilmStorage.getFilms().size(), "Количество фильмов не совпадает");
        assertEquals(updateFilm, inMemoryFilmStorage.getFilms().get(1L), "Фильмы не совпадают");
    }

    @Test
    public void updateUnknownFilmFailTest() {
        Film updateFilm = new Film("Film Updated", "New film update description", LocalDate.of(1967, 3, 25), 100);
        updateFilm.setId(9999L);
        filmController.createFilm(film);

        Assertions.assertThrows(MissingFilmException.class,
                () -> filmController.updateFilm(updateFilm));
        assertEquals(1, inMemoryFilmStorage.getFilms().size(), "Количество фильмов не совпадает");
        assertEquals(film, inMemoryFilmStorage.getFilms().get(1L), "Фильмы не совпадают");
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

    @Test
    public void getFilmByIdNormalTest() {
        filmController.createFilm(film);
        Film testFilm = filmController.getFilmById(1L);

        assertEquals(film, testFilm, "Фильмы не совпадают");
    }

    @Test
    public void getFilmByIdIncorrectIdTest() {
        filmController.createFilm(film);

        Assertions.assertThrows(MissingFilmException.class,
                () -> filmController.getFilmById(9999L), "Должна быть ошибка MissingFilmException");
    }

    @Test
    public void addLikeNormalTest() {
        filmController.createFilm(film);
        filmController.addLike(1L, 1L);

        assertEquals(1, filmController.getFilmById(1L).getLikes().size(), "Количество лайков не совпадает");
        assertTrue(filmController.getFilmById(1L).getLikes().contains(1L), "Должен быть лайк от пользователя с id = 1L");
    }

    @Test
    public void deleteLikeNormalTest() {
        filmController.createFilm(film);
        filmController.addLike(1L, 1L);
        filmController.addLike(1L, 2L);

        filmController.deleteLike(1L, 1L);

        assertEquals(1, filmController.getFilmById(1L).getLikes().size(), "Количество лайков не совпадает");
        assertFalse(filmController.getFilmById(1L).getLikes().contains(1L), "Не должгно быть лайка от пользователя с id = 1L");
    }

    @Test
    public void getPopularFilmsNormalTest() {
        filmController.createFilm(film);
        Film newFilm = new Film("Film Updated", "New film update description", LocalDate.of(1967, 3, 25), 100);
        filmController.createFilm(newFilm);
        filmController.addLike(1L, 1L);
        filmController.addLike(1L, 2L);
        filmController.addLike(2L, 2L);

        Collection<Film> bestFilms = filmController.getPopularFilms(1);

        assertEquals(1, bestFilms.size(), "Количество лучших фильмов не совпадает");
        assertTrue(bestFilms.contains(film), "Лучший фильм не совпадает");
    }
}
