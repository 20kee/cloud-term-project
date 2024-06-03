package com.example.webserver.dto.review;

import com.example.webserver.entity.User;

import java.time.LocalDate;

public record GetReviewsPagesResponse(
        String description,
        LocalDate createdAt,
        int reviewScore,
        double normReviewScore,
        String username,
        Long userId
) {}
