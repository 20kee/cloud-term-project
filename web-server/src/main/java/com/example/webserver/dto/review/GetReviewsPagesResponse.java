package com.example.webserver.dto.review;

import com.example.webserver.entity.User;

import java.text.DecimalFormat;
import java.time.LocalDate;

public record GetReviewsPagesResponse(
        String description,
        LocalDate createdAt,
        int reviewScore,
        String normReviewScore,
        String username,
        Long userId
) {
    static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public GetReviewsPagesResponse(String description, LocalDate createdAt, int reviewScore, double normReviewScore, String username, Long userId) {
        this(description, createdAt, reviewScore, decimalFormat.format(normReviewScore * 100), username, userId);    }
}
