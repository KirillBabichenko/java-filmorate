package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface DaoMpa {
    Optional<Mpa> getMpaById(Long id);

    Collection<Mpa> getAllMpa();
}
