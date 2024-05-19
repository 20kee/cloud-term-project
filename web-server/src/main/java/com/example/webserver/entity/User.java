package com.example.webserver.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_tb")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String username;

    double averageScore;

    public User(String username, double averageScore) {
        this.username = username;
        this.averageScore = averageScore;
    }

    public void setId(Long id) {
        if (this.id == null) this.id = id;
    }
}
