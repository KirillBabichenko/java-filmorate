package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DbGenreService;

import java.util.Collection;

@Slf4j
@Getter
@Setter
@RestController
@RequiredArgsConstructor
public class GenreController {
    final private DbGenreService dbGenreService;

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Long id) {
        return dbGenreService.getGenreById(id);
    }

    @GetMapping("/genres")
    public Collection<Genre> getAllGenres() {
        return dbGenreService.getAllGenres();
    }
}


