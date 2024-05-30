package com.example.webserver.repository;

import com.example.webserver.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlaceRepository extends JpaRepository<Place, Long>, PagingAndSortingRepository<Place, Long> {
    Place findByName(String name);

    @Query("SELECT DISTINCT p FROM Place p LEFT JOIN FETCH p.tags WHERE p.isNearByPNU = true")
    Page<Place> findPlacesWhereNearByPNU(Pageable pageable);
}
