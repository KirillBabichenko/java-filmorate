package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.DaoMpaService;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MpaDaoTest {
    private final DaoMpaService daoMpaService;

    @Test
    public void getMpaByIdTest() {
        Optional<Mpa> mpaOptional = daoMpaService.getMpaById(1L);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                )
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    public void getAllMpaTest() {
        Mpa mpa1 = new Mpa(1, "G");
        Mpa mpa2 = new Mpa(2, "PG");
        Mpa mpa3 = new Mpa(3, "PG-13");
        Mpa mpa4 = new Mpa(4, "R");
        Mpa mpa5 = new Mpa(5, "NC-17");
        Collection<Mpa> allMpa = daoMpaService.getAllMpa();
        assertThat(allMpa)
                .isNotEmpty()
                .hasSize(5)
                .contains(mpa1, mpa2, mpa3, mpa4, mpa5);
    }
}
