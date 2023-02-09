package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmServiceInt {

    Film saveFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    Collection<Film> getAllFilms();

    boolean addLike(Long idFilm, Long idUser);

    Collection<Film> getPopularFilms(Integer amount);

    boolean deleteLike(Long id, Long userId);
}
