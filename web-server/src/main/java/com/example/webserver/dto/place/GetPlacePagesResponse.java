package com.example.webserver.dto.place;

import java.text.DecimalFormat;
import java.util.List;

public record GetPlacePagesResponse(
        Long id,
        String name,
        String averageScore,
        String normScore,
        String address,
        String reviewNumber,

        String tags
) {
    static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public GetPlacePagesResponse(Long id, String name, double averageScore, double normScore, String address, String reviewNumber, List<String> tags) {
        this(id, name, decimalFormat.format(averageScore), decimalFormat.format(normScore * 100), address, reviewNumber, String.join(",", tags));
    }
}
