package com.example.webserver.dto.place;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public record GetPlaceResponse(String placeName,
                               long reviewNumber,
                               String averageScore,
                               String averageNormScore,
                               String address,
                               List<String> tags) {
    static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public GetPlaceResponse(String placeName, long reviewNumber, double averageScore, double averageNormScore, String address, List<String> tags) {
        this(placeName, reviewNumber, decimalFormat.format(averageScore), decimalFormat.format(averageNormScore * 100), address, tags);    }
}
