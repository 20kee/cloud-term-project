package com.example.webserver.dto.init;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RestaurantList(
        List<RestaurantInfo> restaurants
) {
    public record RestaurantInfo(
            String name,
            @JsonProperty("restaurant_type") String tags,
            String averageScore,
            String normScore,
            String reviewCount,
            String address1,
            String address2
    ) {}
}
