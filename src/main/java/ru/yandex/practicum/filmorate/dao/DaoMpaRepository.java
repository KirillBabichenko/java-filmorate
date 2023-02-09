package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DaoMpaRepository implements DaoMpa {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Mpa> getMpaById(Long id) {
        String sql = "SELECT * FROM rating WHERE id_rating = ?";
        List<Mpa> mpa = jdbcTemplate.query(sql, (rs, rowNum) -> createMpa(rs), id);
        if (mpa.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(mpa.get(0));
    }

    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT * FROM rating LIMIT 100";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createMpa(rs));
    }

    private Mpa createMpa(ResultSet rs) throws SQLException {
        return new Mpa(
                rs.getInt("id_rating"),
                rs.getString("rating_name")
        );
    }
}

