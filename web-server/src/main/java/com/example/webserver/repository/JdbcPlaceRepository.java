package com.example.webserver.repository;

import com.example.webserver.entity.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcPlaceRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<Place> places) {
        jdbcTemplate.batchUpdate("INSERT INTO place_tb (name, average_score, average_norm_score) VALUES (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Place place = places.get(i);
                        int parameterIndex = 1;
                        //set name
                        ps.setString(parameterIndex, place.getPlaceName());
                        parameterIndex = parameterIndex + 1;
                        //set average_score
                        ps.setDouble(parameterIndex, place.getAverageScore());
                        parameterIndex = parameterIndex + 1;
                        //set average_norm_score
                        ps.setDouble(parameterIndex, place.getAverageNormScore());
                    }

                    @Override
                    public int getBatchSize() {
                        return places.size();
                    }
                });

        long firstIndex = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);

        for (Place place: places) {
            place.setId(firstIndex);
            firstIndex = firstIndex + 1;
        }
    }
}
