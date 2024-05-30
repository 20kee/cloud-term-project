package com.example.webserver.dto;

import java.text.DecimalFormat;
import java.util.List;

public record GetPlacePagesResponse(
        String name,
        String averageScore,
        String normScore,
        String address,
        String reviewNumber,

        String tags
) {
    static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public GetPlacePagesResponse(String name, double averageScore, double normScore, String address, String reviewNumber, List<String> tags) {
        this(name, decimalFormat.format(averageScore), decimalFormat.format(normScore * 100), address, reviewNumber, String.join(",", tags));
    }
}
