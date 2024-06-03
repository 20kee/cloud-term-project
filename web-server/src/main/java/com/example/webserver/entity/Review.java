package com.example.webserver.entity;

import com.example.webserver.dto.review.GetReviewsPagesResponse;
import com.example.webserver.dto.review.GetUserReviewsPagesResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_tb")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private LocalDate createdAt;

    private int reviewScore;

    private double normReviewScore;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    Place place;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Builder
    public Review(String description, LocalDate createdAt, int reviewScore, double normReviewScore, Place place, User user) {
        this.description = description;
        this.createdAt = createdAt;
        this.reviewScore = reviewScore;
        this.normReviewScore = normReviewScore;
        this.place = place;
        this.user = user;
    }

    public void setId(Long id) {
        if (this.id == null) this.id = id;
    }

    public GetReviewsPagesResponse mapToGetReviewsPagesResponse() {
        return new GetReviewsPagesResponse(
                this.description,
                this.createdAt,
                this.reviewScore,
                this.normReviewScore,
                this.user.getUsername(),
                this.user.getId());
    }

    public GetUserReviewsPagesResponse mapToGetUserReviewsPagesResponse() {
        return new GetUserReviewsPagesResponse(
                this.place.getPlaceName(),
                this.reviewScore,
                this.normReviewScore,
                this.createdAt,
                this.description
        );
    }
}
