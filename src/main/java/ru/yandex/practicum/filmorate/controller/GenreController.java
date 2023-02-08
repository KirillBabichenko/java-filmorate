package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DbGenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    final private DbGenreService dbGenreService;

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Long id) {
        return dbGenreService.getGenreById(id);
    }

    @GetMapping
    public Collection<Genre> getAllGenres() {
        return dbGenreService.getAllGenres();
    }
}
