package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmControllerTest {
    private final FilmController filmController;
    private final UserController userController;
    private User user;
    private Film film;
    private Film secondFilm;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .mpa(new Mpa(1, "G"))
                .genres(new ArrayList<>())
                .build();
        secondFilm = Film.builder()
                .name("updateFilm")
                .description("updateFilm description")
                .releaseDate(LocalDate.of(1977, 3, 25))
                .duration(150)
                .mpa(new Mpa(1, "G"))
                .genres(new ArrayList<>())
                .build();
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .friends(new HashSet<>())
                .unverifiedFriends(new HashSet<>())
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createFilmNormalTest() {
        filmController.createFilm(film);
        film.setId(1L);

        assertEquals(1, filmController.getAllFilms().size(), "Количество фильмов не совпадает");
        assertTrue(filmController.getAllFilms().contains(film), "Фильмы не совпадают");
    }

    @Test
    public void createFilmFailNameTest() {
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Количество ошибок не совпадает");
    }

    @Test
    public void createFilmFailDescriptionTest() {
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», " +
                "стал кандидатом Коломбани.");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Количество ошибок не совпадает");
    }

    @Test
    public void createFilmFailReleaseDateTest() {
        film.setReleaseDate(LocalDate.of(1825, 3, 25));

        Assertions.assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals(0, filmController.getAllFilms().size(), "Количество фильмов не совпадает");
    }

    @Test
    public void createFilmFailDurationTest() {
        film.setDuration(-100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Количество ошибок не совпадает");
    }

    @Test
    public void updateFilmNormalTest() {
        secondFilm.setId(1L);
        filmController.createFilm(film);
        filmController.updateFilm(secondFilm);

        assertEquals(1, filmController.getAllFilms().size(), "Количество фильмов не совпадает");
        assertEquals(secondFilm, filmController.getFilmById(1L), "Фильмы не совпадают");
    }

    @Test
    public void updateUnknownFilmFailTest() {
        secondFilm.setId(9999L);
        filmController.createFilm(film);
        film.setId(1L);

        Assertions.assertThrows(DatabaseException.class,
                () -> filmController.updateFilm(secondFilm));
        assertEquals(1, filmController.getAllFilms().size(), "Количество фильмов не совпадает");
        assertEquals(film, filmController.getFilmById(1L), "Фильмы не совпадают");
    }

    @Test
    public void getAllFilmsTest() {
        filmController.createFilm(film);
        filmController.createFilm(secondFilm);
        Collection<Film> allFilms = filmController.getAllFilms();
        film.setId(1L);

        assertEquals(2, allFilms.size(), "Количество фильмов не совпадает");
        assertTrue(allFilms.contains(film), "Фильм не найден");
    }

    @Test
    public void getFilmByIdNormalTest() {
        filmController.createFilm(film);
        Film testFilm = filmController.getFilmById(1L);
        film.setId(1L);

        assertEquals(film, testFilm, "Фильмы не совпадают");
    }

    @Test
    public void getFilmByIdIncorrectIdTest() {
        filmController.createFilm(film);

        Assertions.assertThrows(DatabaseException.class,
                () -> filmController.getFilmById(9999L), "Должна быть ошибка DatabaseException");
    }

    @Test
    public void addLikeNormalTest() {
        filmController.createFilm(film);
        filmController.createFilm(secondFilm);
        userController.createUser(user);
        filmController.addLike(2L, 1L);
        secondFilm.setId(2L);

        assertTrue(filmController.getPopularFilms(1).contains(secondFilm),
                "В списке должен быть secondFilm");
    }

    @Test
    public void deleteLikeNormalTest() {
        filmController.createFilm(film);
        filmController.createFilm(secondFilm);
        userController.createUser(user);
        filmController.addLike(2L, 1L);
        film.setId(1L);
        secondFilm.setId(2L);
        assertTrue(filmController.getPopularFilms(1).contains(secondFilm),
                "В списке должен быть secondFilm");

        filmController.addLike(1L, 1L);
        filmController.deleteLike(2L, 1L);

        assertTrue(filmController.getPopularFilms(1).contains(film),
                "В списке должен быть film");
    }

    @Test
    public void getPopularFilmsNormalTest() {
        filmController.createFilm(film);
        filmController.createFilm(secondFilm);
        userController.createUser(user);
        filmController.addLike(2L, 1L);
        secondFilm.setId(2L);

        Collection<Film> bestFilms = filmController.getPopularFilms(1);

        assertEquals(1, bestFilms.size(), "Количество лучших фильмов не совпадает");
        assertTrue(bestFilms.contains(secondFilm), "Лучший фильм не совпадает");
    }
}