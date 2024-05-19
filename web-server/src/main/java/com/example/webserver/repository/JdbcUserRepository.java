package com.example.webserver.repository;

import com.example.webserver.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<User> users) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.batchUpdate("INSERT INTO user_tb (username, average_score) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        User user = users.get(i);
                        int parameterIndex = 1;
                        //set username
                        ps.setString(parameterIndex, user.getUsername());
                        parameterIndex = parameterIndex + 1;
                        //set averageScore
                        ps.setDouble(parameterIndex, user.getAverageScore());
                        parameterIndex = parameterIndex + 1;
                    }

                    @Override
                    public int getBatchSize() {
                        return users.size();
                    }
                });


        long firstIndex = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);

        for (User user : users) {
            user.setId(firstIndex);
            firstIndex = firstIndex + 1;
        }
    }
}
