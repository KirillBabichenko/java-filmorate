package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoGenre;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;

@Service
@RequiredArgsConstructor
public class DbGenreService {
    private final DaoGenre daoGenre;

    public Genre getGenreById(Long id) {
        checkForPositivity(id);
        return daoGenre.getGenreById(id).orElseThrow(() ->
                new DatabaseException("При запросе жанра произошла ошибка"));
    }

    public Collection<Genre> getAllGenres() {
        return daoGenre.getAllGenres();
    }
}
