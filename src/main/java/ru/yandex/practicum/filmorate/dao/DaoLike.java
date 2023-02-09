package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DaoLike {

    boolean addLike(Long idFilm, Long idUser);

    boolean deleteLike(Long id, Long userId);

    List<Film> getPopularFilms(Integer amount);
}

