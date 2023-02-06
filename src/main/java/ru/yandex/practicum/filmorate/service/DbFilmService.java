package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoFilmService;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;
import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivityCount;

@Service
@RequiredArgsConstructor
@Slf4j
public class DbFilmService implements FilmStorage {
    private final LocalDate startDate = LocalDate.of(1895, Month.DECEMBER, 28);
    private final DaoFilmService daoFilmService;

    @Override
    public Film createFilm(Film film) {
        Film validateFilm = validateFilm(film);
        Optional<Film> filmOptional = daoFilmService.saveFilm(validateFilm);
        if (filmOptional.isPresent()) {
            return filmOptional.get();
        } else log.info("При записи фильма в базу данных произошла ошибка");
        throw new DatabaseException("При записи фильма в базу данных произошла ошибка");
    }

    @Override
    public Film updateFilm(Film film) {
        Film validateFilm = validateFilm(film);
        Optional<Film> filmOptional = daoFilmService.updateFilm(validateFilm);
        if (filmOptional.isPresent()) {
            return filmOptional.get();
        } else log.info("При обновлении фильма в базе данных произошла ошибка");
        throw new DatabaseException("При обновлении фильма в базе данных произошла ошибка");
    }

    @Override
    public Film getFilmById(Long id) {
        checkForPositivity(id);
        Optional<Film> filmOptional = daoFilmService.getFilmById(id);
        if (filmOptional.isPresent()) {
            return filmOptional.get();
        } else log.info("Фильма с таким айди не существует");
        throw new DatabaseException("При запросе фильма в базе данных произошла ошибка. Фильма с таким айди нет в базе");
    }

    @Override
    public Collection<Film> getAllFilms() {
        return daoFilmService.getAllFilms();
    }

    public boolean addLike(Long idFilm, Long idUser) {
        checkForPositivity(idFilm);
        checkForPositivity(idUser);
        if (!daoFilmService.addLike(idFilm, idUser)) {
            throw new DatabaseException("При добавлении лайка произошла ошибка.");
        }
        return true;
    }

    public Collection<Film> getPopularFilms(Integer amount) {
        checkForPositivityCount(amount);
        return daoFilmService.getPopularFilms(amount);
    }

    public boolean deleteLike(Long id, Long userId) {
        checkForPositivity(id);
        checkForPositivity(userId);
        if (!daoFilmService.deleteLike(id, userId)) {
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
