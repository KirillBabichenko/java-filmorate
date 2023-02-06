package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DbFilmService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@Getter
@Setter
@RestController
@RequiredArgsConstructor
public class FilmController {
    final private DbFilmService dbFilmService;

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return dbFilmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return dbFilmService.updateFilm(film);
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return dbFilmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return dbFilmService.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public boolean addLike(@PathVariable Long id, @PathVariable Long userId) {
        return dbFilmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return dbFilmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        return dbFilmService.getPopularFilms(count);
    }
}
