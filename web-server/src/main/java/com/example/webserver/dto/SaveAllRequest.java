package com.example.webserver.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SaveAllRequest(
        List<ReviewUsernameDto> reviews
) {
        public record ReviewUsernameDto(
                String username,
                List<ReviewDto> reviewList
        ) {
            public record ReviewDto(
                    String restaurantName,
                    int reviewScore,
                    String description,
                    String createdAt
            ) {}
        }
}
