package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.DaoFilmService;
import ru.yandex.practicum.filmorate.dao.DaoUserService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDaoTest {
    private final DaoFilmService daoFilmService;
    private final DaoUserService daoUserService;
    private Film film;
    private Film secondFilm;
    private User user;

    @BeforeEach
    public void beforeEach() {
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
                .build();
    }

    @Test
    public void saveFilmNormalTest() {
        Optional<Film> filmOptional = daoFilmService.saveFilm(film);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                )
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "nisi eiusmod")
                );
    }

    @Test
    public void getFilmNormalTest() {
        daoFilmService.saveFilm(film);
        Optional<Film> filmOptional = daoFilmService.getFilmById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                )
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "nisi eiusmod")
                );
    }

    @Test
    public void getMissingFilmTest() {
        Optional<Film> filmOptional = daoFilmService.getFilmById(999L);
        assertThat(filmOptional)
                .isEmpty();
    }

    @Test
    public void updateFilmNormalTest() {
        daoFilmService.saveFilm(film);
        secondFilm.setId(1L);
        Optional<Film> filmOptional = daoFilmService.updateFilm(secondFilm);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                )
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "updateFilm")
                );
    }


    @Test
    public void getAllFilmsNormalTest() {
        daoFilmService.saveFilm(film);
        daoFilmService.saveFilm(secondFilm);
        secondFilm.setId(2L);
        Collection<Film> allFilms = daoFilmService.getAllFilms();
        assertThat(allFilms)
                .isNotEmpty()
                .hasSize(2)
                .contains(secondFilm);
    }

    @Test
    public void getPopularFilmsNormalTest() {
        daoFilmService.saveFilm(film);
        film.setId(1L);
        Collection<Film> allFilms = daoFilmService.getPopularFilms(10);
        assertThat(allFilms)
                .isNotEmpty()
                .hasSize(1)
                .contains(film);
    }

    @Test
    public void addLikeNormalTest() {
        daoFilmService.saveFilm(film);
        daoFilmService.saveFilm(secondFilm);
        secondFilm.setId(2L);
        daoUserService.saveUser(user);

        daoFilmService.addLike(2L, 1L);
        Collection<Film> secondFilmBest = daoFilmService.getPopularFilms(1);
        assertThat(secondFilmBest)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondFilm);
    }

    @Test
    public void deleteLikeNormalTest() {
        daoFilmService.saveFilm(film);
        daoFilmService.saveFilm(secondFilm);
        film.setId(1L);
        secondFilm.setId(2L);
        daoUserService.saveUser(user);
        daoFilmService.addLike(1L, 1L);
        Collection<Film> filmFilmBest = daoFilmService.getPopularFilms(1);
        assertThat(filmFilmBest)
                .isNotEmpty()
                .hasSize(1)
                .contains(film);

        daoFilmService.addLike(2L, 1L);
        daoFilmService.deleteLike(1L, 1L);
        Collection<Film> secondFilmBest = daoFilmService.getPopularFilms(1);
        assertThat(secondFilmBest)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondFilm);
    }
}

