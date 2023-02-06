package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.DaoGenreService;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreDaoTest {
    private final DaoGenreService daoGenreService;

    @Test
    public void getGenreByIdTest() {
        Optional<Genre> genreOptional = daoGenreService.getGenreById(1L);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
                )
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void getAllMpaTest() {
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(2, "Драма");
        Genre genre3 = new Genre(3, "Мультфильм");
        Genre genre4 = new Genre(4, "Триллер");
        Genre genre5 = new Genre(5, "Документальный");
        Genre genre6 = new Genre(6, "Боевик");
        Collection<Genre> allGenre = daoGenreService.getAllGenres();
        assertThat(allGenre)
                .isNotEmpty()
                .hasSize(6)
                .contains(genre1, genre2, genre3, genre4, genre5, genre6);
    }
}
