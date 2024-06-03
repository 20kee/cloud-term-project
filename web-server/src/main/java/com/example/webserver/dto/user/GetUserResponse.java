package com.example.webserver.dto.user;

import java.text.DecimalFormat;
import java.time.LocalDate;

public record GetUserResponse(
        String username,
        String averageReviewScore
) {
    static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public GetUserResponse(String username, double averageReviewScore) {
        this(username, decimalFormat.format(averageReviewScore));    }
}
