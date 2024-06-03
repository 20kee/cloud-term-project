package com.example.webserver.entity;

import com.example.webserver.dto.GetPlacePagesResponse;
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

    private String placeName;

    private long reviewNumber;

    private double averageScore;

    private double averageNormScore;

    private boolean isNearByPNU;

    private String address;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "place_tag", joinColumns = @JoinColumn(name = "place_id"))
    private List<String> tags = new ArrayList<>();

    public Place(String placeName) {
        this.placeName = placeName;
    }

    @Builder
    public Place(String placeName, String tags, long reviewNumber, double averageScore, double averageNormScore, boolean isNearByPNU, String address1, String address2) {
        this.placeName = placeName;
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
        return new GetPlacePagesResponse(this.placeName,
                this.averageScore,
                this.averageNormScore,
                this.address,
                String.valueOf(this.reviewNumber),
                this.tags);
    }
}
