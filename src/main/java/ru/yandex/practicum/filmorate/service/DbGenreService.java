package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoGenreRepository;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;

@Service
@RequiredArgsConstructor
public class DbGenreService {
    private final DaoGenreRepository daoGenreRepository;

    public Genre getGenreById(Long id) {
        checkForPositivity(id);
        return daoGenreRepository.getGenreById(id).orElseThrow(() ->
                new DatabaseException("При запросе жанра произошла ошибка"));
    }

    public Collection<Genre> getAllGenres() {
        return daoGenreRepository.getAllGenres();
    }
}
