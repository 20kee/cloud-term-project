package com.example.webserver.dto.place;

import java.util.List;

public record GetPlaceResponse(String placeName,
                               long reviewNumber,
                               double averageScore,
                               double averageNormScore,
                               String address,
                               List<String> tags) {
}
