package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface DaoFilm {

    Optional<Film> saveFilm(Film film);

    Optional<Film> getFilmById(Long id);

    List<Film> getAllFilms();

    boolean addLike(Long idFilm, Long idUser);

    boolean deleteLike(Long id, Long userId);

    List<Film> getPopularFilms(Integer amount);

    Optional<Film> updateFilm(Film film);
}
