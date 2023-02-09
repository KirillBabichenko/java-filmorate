package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.DbMpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    final private DbMpaService dbMpaService;

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        return dbMpaService.getMpaById(id);
    }

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return dbMpaService.getAllMpa();
    }
}