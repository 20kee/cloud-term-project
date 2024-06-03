package com.example.webserver.repository;

import com.example.webserver.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<Review> reviews) {
        jdbcTemplate.batchUpdate("INSERT INTO review_tb (user_id, place_id, description, created_at, review_score, norm_review_score) VALUES (?, ?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Review review = reviews.get(i);
                        int parameterIndex = 1;
                        //set userId
                        ps.setLong(parameterIndex, review.getUser().getId());
                        parameterIndex = parameterIndex + 1;

                        //set placeId
                        ps.setLong(parameterIndex, review.getPlace().getId());
                        parameterIndex = parameterIndex + 1;

                        //set description
                        ps.setString(parameterIndex, review.getDescription());
                        parameterIndex = parameterIndex + 1;
                        //set createdAt
                        ps.setTimestamp(parameterIndex, Timestamp.valueOf(review.getCreatedAt().atStartOfDay()));
                        parameterIndex = parameterIndex + 1;
                        //set reviewScore
                        ps.setInt(parameterIndex, review.getReviewScore());
                        parameterIndex = parameterIndex + 1;
                        //set normReviewScore
                        ps.setDouble(parameterIndex, review.getNormReviewScore());
                        parameterIndex = parameterIndex + 1;
                    }

                    @Override
                    public int getBatchSize() {
                        return reviews.size();
                    }
                });

        long firstIndex = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);

        for (Review review: reviews) {
            review.setId(firstIndex);
            firstIndex = firstIndex + 1;
        }
    }
}
