package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.DaoFilmRepository;
import ru.yandex.practicum.filmorate.dao.DaoLikeRepository;
import ru.yandex.practicum.filmorate.dao.DaoUserRepository;
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
    private final DaoFilmRepository daoFilmRepository;
    private final DaoUserRepository daoUserRepository;
    private final DaoLikeRepository daoLikeRepository;
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
        Optional<Film> filmOptional = daoFilmRepository.saveFilm(film);
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
        daoFilmRepository.saveFilm(film);
        Optional<Film> filmOptional = daoFilmRepository.getFilmById(1L);
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
        Optional<Film> filmOptional = daoFilmRepository.getFilmById(999L);
        assertThat(filmOptional)
                .isEmpty();
    }

    @Test
    public void updateFilmNormalTest() {
        daoFilmRepository.saveFilm(film);
        secondFilm.setId(1L);
        Optional<Film> filmOptional = daoFilmRepository.updateFilm(secondFilm);
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
        daoFilmRepository.saveFilm(film);
        daoFilmRepository.saveFilm(secondFilm);
        secondFilm.setId(2L);
        Collection<Film> allFilms = daoFilmRepository.getAllFilms();
        assertThat(allFilms)
                .isNotEmpty()
                .hasSize(2)
                .contains(secondFilm);
    }

    @Test
    public void getPopularFilmsNormalTest() {
        daoFilmRepository.saveFilm(film);
        film.setId(1L);
        Collection<Film> allFilms = daoLikeRepository.getPopularFilms(10);
        assertThat(allFilms)
                .isNotEmpty()
                .hasSize(1)
                .contains(film);
    }

    @Test
    public void addLikeNormalTest() {
        daoFilmRepository.saveFilm(film);
        daoFilmRepository.saveFilm(secondFilm);
        secondFilm.setId(2L);
        daoUserRepository.saveUser(user);

        daoLikeRepository.addLike(2L, 1L);
        Collection<Film> secondFilmBest = daoLikeRepository.getPopularFilms(1);
        assertThat(secondFilmBest)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondFilm);
    }

    @Test
    public void deleteLikeNormalTest() {
        daoFilmRepository.saveFilm(film);
        daoFilmRepository.saveFilm(secondFilm);
        film.setId(1L);
        secondFilm.setId(2L);
        daoUserRepository.saveUser(user);
        daoLikeRepository.addLike(1L, 1L);
        Collection<Film> filmFilmBest = daoLikeRepository.getPopularFilms(1);
        assertThat(filmFilmBest)
                .isNotEmpty()
                .hasSize(1)
                .contains(film);

        daoLikeRepository.addLike(2L, 1L);
        daoLikeRepository.deleteLike(1L, 1L);
        Collection<Film> secondFilmBest = daoLikeRepository.getPopularFilms(1);
        assertThat(secondFilmBest)
                .isNotEmpty()
                .hasSize(1)
                .contains(secondFilm);
    }
}

