package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DaoMpaRepository;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.validation.PositivityChecker.checkForPositivity;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbMpaService {
    private final DaoMpaRepository daoMpaRepository;

    public Mpa getMpaById(Long id) {
        checkForPositivity(id);
        return daoMpaRepository.getMpaById(id).orElseThrow(() ->
                new DatabaseException("При запросе MPA произошла ошибка"));
    }

    public Collection<Mpa> getAllMpa() {
        Collection<Mpa> mpaList = daoMpaRepository.getAllMpa();
        if (!mpaList.isEmpty()) {
            return mpaList;
        } else log.info("При запросе MPA произошла ошибка");
        throw new DatabaseException("При запросе MPA произошла ошибка");
    }
}

