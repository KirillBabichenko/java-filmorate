package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.DbMpaService;

import java.util.Collection;

@Slf4j
@Getter
@Setter
@RestController
@RequiredArgsConstructor
public class MpaController {
    final private DbMpaService dbMpaService;

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        return dbMpaService.getMpaById(id);
    }

    @GetMapping("/mpa")
    public Collection<Mpa> getAllMpa() {
        return dbMpaService.getAllMpa();
    }
}

