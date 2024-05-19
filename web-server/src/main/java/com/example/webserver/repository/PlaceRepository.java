package com.example.webserver.repository;

import com.example.webserver.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Place findByName(String name);
}
