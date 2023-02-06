package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoMpaService;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;

@Service
@RequiredArgsConstructor
@Slf4j
public class DbMpaService {
    private final DaoMpaService daoMpaService;

    public Mpa getMpaById(Long id) {
        checkForPositivity(id);
        Optional<Mpa> mpaOptional = daoMpaService.getMpaById(id);
        if (mpaOptional.isPresent()) {
            return mpaOptional.get();
        } else log.info("При запросе MPA произошла ошибка");
        throw new DatabaseException("При запросе MPA произошла ошибка");
    }

    public Collection<Mpa> getAllMpa() {
        Collection<Mpa> mpaList = daoMpaService.getAllMpa();
        if (!mpaList.isEmpty()) {
            return mpaList;
        } else log.info("При запросе MPA произошла ошибка");
        throw new DatabaseException("При запросе MPA произошла ошибка");
    }
}

