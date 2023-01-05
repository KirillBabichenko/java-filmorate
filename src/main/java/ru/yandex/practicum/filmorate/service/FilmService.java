package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

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
}
