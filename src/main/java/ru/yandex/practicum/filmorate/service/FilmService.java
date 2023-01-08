package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;

    public Film addLike(Long idFilm, Long idUser) {
        inMemoryFilmStorage.getFilms().get(idFilm).getLikes().add(idUser);
        return inMemoryFilmStorage.getFilms().get(idFilm);
    }

    public Film deleteLike(Long idFilm, Long idUser) {
        inMemoryFilmStorage.getFilms().get(idFilm).getLikes().remove(idUser);
        return inMemoryFilmStorage.getFilms().get(idFilm);
    }

    public Collection<Film> getPopularFilms(Integer amount) {
        return inMemoryFilmStorage.getFilms().values().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(film -> film.getLikes().size())))
                .limit(amount)
                .collect(Collectors.toList());
    }

    public void checkForPositivity(Long id) {
        if (id <= 0) {
            throw new IncorrectParameterException("Некорректно указан параметр - " + id + ". Должен быть больше нуля.");
        }
    }

    public void checkForPositivityCount(Integer count) {
        if (count <= 0) {
            throw new IncorrectParameterException("Некорректно указан параметр - " + count + ". Должен быть больше нуля.");
        }
    }
}
