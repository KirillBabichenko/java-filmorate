package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoFilmRepository;
import ru.yandex.practicum.filmorate.dao.DaoLikeRepository;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;
import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivityCount;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbFilmService implements FilmServiceInt {
    private final LocalDate startDate = LocalDate.of(1895, Month.DECEMBER, 28);
    private final DaoFilmRepository daoFilmRepository;
    private final DaoLikeRepository daoLikeRepository;

    @Override
    public Film saveFilm(Film film) {
        Film validateFilm = validateFilm(film);
        return daoFilmRepository.saveFilm(validateFilm).orElseThrow(() ->
                new DatabaseException("При записи фильма в базу данных произошла ошибка"));
    }

    @Override
    public Film updateFilm(Film film) {
        Film validateFilm = validateFilm(film);
        return daoFilmRepository.updateFilm(validateFilm).orElseThrow(() ->
                new DatabaseException("При обновлении фильма в базе данных произошла ошибка"));
    }

    @Override
    public Film getFilmById(Long id) {
        checkForPositivity(id);
        return daoFilmRepository.getFilmById(id).orElseThrow(() ->
                new DatabaseException("При запросе фильма в базе данных произошла ошибка." +
                        " Фильма с таким айди нет в базе"));
    }

    @Override
    public Collection<Film> getAllFilms() {
        return daoFilmRepository.getAllFilms();
    }

    @Override
    public boolean addLike(Long idFilm, Long idUser) {
        checkForPositivity(idFilm);
        checkForPositivity(idUser);
        if (!daoLikeRepository.addLike(idFilm, idUser)) {
            throw new DatabaseException("При добавлении лайка произошла ошибка.");
        }
        return true;
    }

    @Override
    public Collection<Film> getPopularFilms(Integer amount) {
        checkForPositivityCount(amount);
        return daoLikeRepository.getPopularFilms(amount);
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        checkForPositivity(id);
        checkForPositivity(userId);
        if (!daoLikeRepository.deleteLike(id, userId)) {
            throw new DatabaseException("При удалении лайка произошла ошибка.");
        }
        return true;
    }

    private Film validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(startDate)) {
            log.info("Ошибка с датой релиза - {}", film);
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года");
        }
        return film;
    }
}
