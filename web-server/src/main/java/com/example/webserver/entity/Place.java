package com.example.webserver.entity;

import com.example.webserver.dto.GetPlacePagesResponse;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place_tb")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    double averageScore;

    double averageNormScore;

    public Place(String name, Double averageScore, Double averageNormScore) {
        this.name = name;
        this.averageScore = averageScore;
        this.averageNormScore = averageNormScore;
    }

    public void setId(Long id) {
        if (this.id == null) this.id = id;
    }

    public GetPlacePagesResponse mapToDto() {
        return new GetPlacePagesResponse(this.name, this.averageScore, this.averageNormScore);
    }
}
