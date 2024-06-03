package com.example.webserver.dto.review;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public record GetUserReviewsPagesResponse(
        String placeName,
        int reviewScore,
        String normReviewScore,
        LocalDate createdAt,
        String description

) {
    static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public GetUserReviewsPagesResponse(String placeName, int reviewScore, double normReviewScore, LocalDate createdAt, String description) {
        this(placeName, reviewScore, decimalFormat.format(normReviewScore * 100), createdAt, description);    }
}
