package com.example.webserver.entity;

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

    public Place(String name, Double averageScore) {
        this.name = name;
        this.averageScore = 0;
        this.averageNormScore = 0;
    }

    public void setId(Long id) {
        if (this.id == null) this.id = id;
    }
}
