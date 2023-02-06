package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoGenreService;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;

@Service
@RequiredArgsConstructor
@Slf4j
public class DbGenreService {
    private final DaoGenreService daoGenreService;

    public Genre getGenreById(Long id) {
        checkForPositivity(id);
        Optional<Genre> genreOptional = daoGenreService.getGenreById(id);
        if (genreOptional.isPresent()) {
            return genreOptional.get();
        } else log.info("При запросе жанра произошла ошибка");
        throw new DatabaseException("При запросе жанра произошла ошибка");
    }

    public Collection<Genre> getAllGenres() {
        return daoGenreService.getAllGenres();
    }
}
