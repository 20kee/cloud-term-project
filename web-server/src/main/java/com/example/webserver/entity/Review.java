package com.example.webserver.entity;

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
    Long id;

    String description;

    LocalDate createdAt;

    int reviewScore;

    double normReviewScore;

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
}
