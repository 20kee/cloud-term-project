package com.example.webserver.entity;

import com.example.webserver.dto.GetPlacePagesResponse;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place_tb")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private long reviewNumber;

    private double averageScore;

    private double averageNormScore;

    private boolean isNearByPNU;

    private String address;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "place_tag", joinColumns = @JoinColumn(name = "place_id"))
    private List<String> tags = new ArrayList<>();

    public Place(String name, long reviewNumber, double averageScore, double averageNormScore) {
        this.name = name;
        this.reviewNumber = reviewNumber;
        this.averageScore = averageScore;
        this.averageNormScore = averageNormScore;
    }

    @Builder
    public Place(String name, String tags, long reviewNumber, double averageScore, double averageNormScore, boolean isNearByPNU, String address1, String address2) {
        this.name = name;
        this.tags = Arrays.stream(tags.split(",")).toList();
        this.reviewNumber = reviewNumber;
        this.averageScore = averageScore;
        this.averageNormScore = averageNormScore;
        this.isNearByPNU = isNearByPNU;
        this.address = address1 + " " + address2;
    }

    public void setId(Long id) {
        if (this.id == null) this.id = id;
    }

    public GetPlacePagesResponse mapToDto() {
        return new GetPlacePagesResponse(this.name,
                this.averageScore,
                this.averageNormScore,
                this.address,
                String.valueOf(this.reviewNumber),
                this.tags);
    }
}
